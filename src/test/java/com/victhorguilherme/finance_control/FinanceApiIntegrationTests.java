package com.victhorguilherme.finance_control;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FinanceApiIntegrationTests {
    @LocalServerPort
    int port;

    @Autowired
    JdbcTemplate jdbc;

    private final ObjectMapper mapper = new ObjectMapper();
    private static final String PASSWORD = "SenhaTeste!123";

    private class Client implements AutoCloseable {
        final CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        final HttpClient http = HttpClient.newBuilder().cookieHandler(cookies).build();
        String token;
        String header;

        HttpResponse<String> call(String method, String path, Object body, boolean csrf) throws Exception {
            var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                    .header("Accept", "application/json");
            if (csrf && token != null) builder.header(header, token);
            if (body != null) builder.header("Content-Type", "application/json");
            return http.send(builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build(),
                    HttpResponse.BodyHandlers.ofString());
        }

        void csrf() throws Exception {
            JsonNode response = json(call("GET", "/auth/csrf", null, false), 200);
            token = response.get("token").asString();
            header = response.get("headerName").asString();
        }

        String sessionId() {
            return cookies.getCookieStore().getCookies().stream()
                    .filter(cookie -> cookie.getName().equals("JSESSIONID"))
                    .map(cookie -> cookie.getValue()).findFirst().orElse("");
        }

        JsonNode create(String path, Object body) throws Exception {
            return json(call("POST", path, body, true), 200);
        }

        void register(String email) throws Exception {
            csrf();
            json(call("POST", "/auth/registrar",
                    Map.of("nome", "Pessoa Teste", "email", email, "senha", PASSWORD), true), 201);
        }

        void login(String email) throws Exception {
            json(call("POST", "/auth/login", Map.of("email", email, "senha", PASSWORD), true), 200);
            csrf(); // O token anterior foi invalidado pelo login.
        }

        @Override
        public void close() {
            http.close();
        }
    }

    private JsonNode json(HttpResponse<String> response, int status) {
        assertEquals(status, response.statusCode(), response.body());
        return mapper.readTree(response.body());
    }

    private String email() {
        return UUID.randomUUID() + "@example.com";
    }

    private Map<String, Object> transacao(long categoria, String tipo, String valor, String data) {
        return Map.of("descricao", "Operação de teste", "categoriaId", categoria, "tipo", tipo,
                "valor", new BigDecimal(valor), "data", data);
    }

    private long id(JsonNode value) {
        return value.get("id").asLong();
    }

    @Test
    void sessionCsrfAndLogoutLifecycle() throws Exception {
        try (var client = new Client(); var anonymous = new Client()) {
            String email = email();
            assertEquals(401, anonymous.call("GET", "/contas/listar", null, false).statusCode());
            assertEquals(403, anonymous.call("POST", "/auth/login",
                    Map.of("email", email, "senha", PASSWORD), false).statusCode());
            client.register(email);
            String previousSession = client.sessionId();
            String previousToken = client.token;
            JsonNode login = json(client.call("POST", "/auth/login",
                    Map.of("email", email.toUpperCase(), "senha", PASSWORD), true), 200);
            assertEquals(email, login.get("email").asString());
            assertEquals(3, login.size());
            assertFalse(login.has("senhaHash"));
            assertFalse(login.has("senha"));
            assertNotEquals(previousSession, client.sessionId());
            JsonNode me = json(client.call("GET", "/auth/me", null, false), 200);
            assertEquals(id(login), id(me));
            assertEquals(403, client.call("POST", "/contas", Map.of("nome", "CSRF antigo"), true).statusCode());
            client.csrf();
            assertNotEquals(previousToken, client.token);
            assertEquals(403, client.call("POST", "/auth/logout", null, false).statusCode());
            assertEquals(204, client.call("POST", "/auth/logout", null, true).statusCode());
            assertEquals(401, client.call("GET", "/auth/me", null, false).statusCode());
            assertEquals(401, client.call("GET", "/contas/listar", null, false).statusCode());
        }
    }

    @Test
    void invalidCredentialsAndRegistrationValidation() throws Exception {
        try (var client = new Client()) {
            String email = email();
            client.register(email);
            var wrong = client.call("POST", "/auth/login",
                    Map.of("email", email, "senha", "SenhaErrada!"), true);
            var missing = client.call("POST", "/auth/login",
                    Map.of("email", email(), "senha", PASSWORD), true);
            assertEquals(401, wrong.statusCode());
            assertEquals(401, missing.statusCode());
            assertEquals(wrong.body(), missing.body());
            assertEquals(400, client.call("POST", "/auth/login",
                    Map.of("email", "invalido", "senha", PASSWORD), true).statusCode());
            assertEquals(400, client.call("POST", "/auth/login",
                    Map.of("email", email, "senha", ""), true).statusCode());
            assertEquals(409, client.call("POST", "/auth/registrar",
                    Map.of("nome", "Duplicado", "email", email.toUpperCase(), "senha", PASSWORD), true).statusCode());
            assertEquals(400, client.call("POST", "/auth/registrar",
                    Map.of("nome", "Senha longa", "email", email(), "senha", "á".repeat(40)), true).statusCode());
            String hash = jdbc.queryForObject("SELECT senha_hash FROM usuarios WHERE email = ?", String.class, email);
            assertNotEquals(PASSWORD, hash);
            assertTrue(hash.startsWith("$2"));
        }
    }

    @Test
    void usersCannotReadOrChangeEachOthersData() throws Exception {
        try (var a = new Client(); var b = new Client()) {
            String emailA = email(), emailB = email();
            a.register(emailA); a.login(emailA);
            b.register(emailB); b.login(emailB);
            long contaA = id(a.create("/contas", Map.of("nome", "Carteira")));
            long categoriaA = id(a.create("/categorias", Map.of("nome", "Salário")));
            long contaB = id(b.create("/contas", Map.of("nome", "Carteira")));
            long categoriaB = id(b.create("/categorias", Map.of("nome", "Salário")));
            long txA = id(a.create("/contas/" + contaA + "/transacoes",
                    transacao(categoriaA, "RECEITA", "100", "2026-09-01")));
            assertEquals(1, json(b.call("GET", "/contas/listar", null, false), 200).size());
            assertEquals(contaB, id(json(b.call("GET", "/contas/listar", null, false), 200).get(0)));
            assertEquals(1, json(b.call("GET", "/categorias/listar", null, false), 200).size());
            assertEquals(0, json(b.call("GET", "/transacoes/listar", null, false), 200).size());
            for (String path : new String[]{"/contas/" + contaA, "/categorias/" + categoriaA,
                    "/transacoes/" + txA, "/contas/" + contaA + "/saldo", "/contas/" + contaA + "/extrato"}) {
                assertEquals(404, b.call("GET", path, null, false).statusCode(), path);
            }
            assertEquals(404, b.call("PUT", "/contas/atualizar/" + contaA,
                    Map.of("nome", "Invadida"), true).statusCode());
            assertEquals(404, b.call("PUT", "/categorias/atualizar/" + categoriaA,
                    Map.of("nome", "Invadida"), true).statusCode());
            assertEquals(404, b.call("PUT", "/transacoes/atualizar/" + txA,
                    transacao(categoriaB, "DESPESA", "1", "2026-09-01"), true).statusCode());
            assertEquals(404, b.call("DELETE", "/contas/excluir/" + contaA, null, true).statusCode());
            assertEquals(404, b.call("DELETE", "/transacoes/excluir/" + txA, null, true).statusCode());
            assertEquals(404, b.call("POST", "/contas/" + contaA + "/transacoes",
                    transacao(categoriaB, "RECEITA", "1", "2026-09-01"), true).statusCode());
            assertEquals(404, b.call("POST", "/contas/" + contaB + "/transacoes",
                    transacao(categoriaA, "RECEITA", "1", "2026-09-01"), true).statusCode());
            assertEquals(404, a.call("PUT", "/transacoes/atualizar/" + txA,
                    transacao(categoriaB, "DESPESA", "1", "2026-09-01"), true).statusCode());
            assertEquals(404, b.call("GET", "/contas/" + contaB + "/extrato?categoriaId=" + categoriaA,
                    null, false).statusCode());
            assertEquals(100, json(a.call("GET", "/contas/" + contaA + "/saldo", null, false), 200).asInt());
            String transactionJson = a.call("GET", "/transacoes/" + txA, null, false).body();
            assertFalse(transactionJson.contains("senha"));
            assertFalse(transactionJson.contains("usuario"));
            assertFalse(transactionJson.contains("email"));
        }
    }

    @Test
    void financeCrudBalanceFiltersAndDeletionGuards() throws Exception {
        try (var client = new Client()) {
            String email = email();
            client.register(email); client.login(email);
            long conta = id(client.create("/contas", Map.of("nome", "Principal")));
            long categoria = id(client.create("/categorias", Map.of("nome", "Receitas")));
            long despesaCategoria = id(client.create("/categorias", Map.of("nome", "Despesas")));
            long receita = id(client.create("/contas/" + conta + "/transacoes",
                    transacao(categoria, "RECEITA", "100", "2026-09-01")));
            long despesa = id(client.create("/contas/" + conta + "/transacoes",
                    transacao(despesaCategoria, "DESPESA", "30", "2026-09-15")));
            assertEquals(70, json(client.call("GET", "/contas/" + conta + "/saldo", null, false), 200).asInt());
            JsonNode filtered = json(client.call("GET", "/contas/" + conta
                    + "/extrato?dataInicio=2026-09-15&dataFim=2026-09-15&categoriaId=" + despesaCategoria, null, false), 200);
            assertEquals(1, filtered.size()); assertEquals(despesa, id(filtered.get(0)));
            assertEquals(0, json(client.call("GET", "/contas/" + conta
                    + "/extrato?dataInicio=2026-09-15&categoriaId=" + categoria, null, false), 200).size());
            assertEquals(400, client.call("GET", "/contas/" + conta
                    + "/extrato?dataInicio=2026-09-20&dataFim=2026-09-01", null, false).statusCode());
            json(client.call("PUT", "/transacoes/atualizar/" + despesa,
                    transacao(despesaCategoria, "DESPESA", "25", "2026-09-16"), true), 200);
            assertEquals(25, json(client.call("GET", "/transacoes/" + despesa, null, false), 200).get("valor").asInt());
            assertEquals(75, jdbc.queryForObject(
                    "SELECT SUM(CASE WHEN tipo = 'RECEITA' THEN valor ELSE -valor END) FROM transacao WHERE conta_id = ?",
                    BigDecimal.class, conta).intValueExact());
            assertEquals(409, client.call("DELETE", "/contas/excluir/" + conta, null, true).statusCode());
            assertEquals(409, client.call("POST", "/contas", Map.of("nome", "PRINCIPAL"), true).statusCode());
            assertEquals(409, client.call("POST", "/categorias", Map.of("nome", "RECEITAS"), true).statusCode());
            assertEquals(409, client.call("PUT", "/categorias/atualizar/" + despesaCategoria,
                    Map.of("nome", "RECEITAS"), true).statusCode());
            json(client.call("PUT", "/contas/atualizar/" + conta, Map.of("nome", "Renomeada"), true), 200);
            assertEquals("Renomeada", json(client.call("GET", "/contas/" + conta, null, false), 200).get("nome").asString());
            for (String valor : new String[]{"-1", "0", "1.234", "100000000000000000"}) {
                assertEquals(400, client.call("POST", "/contas/" + conta + "/transacoes",
                        transacao(categoria, "RECEITA", valor, "2026-09-01"), true).statusCode());
            }
            assertEquals(404, client.call("POST", "/contas/" + conta + "/transacoes",
                    transacao(Long.MAX_VALUE, "RECEITA", "1", "2026-09-01"), true).statusCode());
            assertEquals(400, client.call("POST", "/contas", Map.of("nome", "x".repeat(256)), true).statusCode());
            assertEquals(204, client.call("DELETE", "/transacoes/excluir/" + receita, null, true).statusCode());
            assertEquals(204, client.call("DELETE", "/transacoes/excluir/" + despesa, null, true).statusCode());
            assertEquals(404, client.call("DELETE", "/transacoes/excluir/" + despesa, null, true).statusCode());
            assertEquals(0, json(client.call("GET", "/contas/" + conta + "/saldo", null, false), 200).asInt());
            assertEquals(204, client.call("DELETE", "/contas/excluir/" + conta, null, true).statusCode());
            assertEquals(404, client.call("GET", "/contas/" + conta, null, false).statusCode());
        }
    }

    @Test
    void legacyRecordsArePreservedButNotExposed() throws Exception {
        String name = "Legado " + UUID.randomUUID();
        jdbc.update("INSERT INTO conta (nome) VALUES (?)", name);
        jdbc.update("INSERT INTO categories (nome) VALUES (?)", name);
        long conta = jdbc.queryForObject("SELECT id FROM conta WHERE nome = ?", Long.class, name);
        long categoria = jdbc.queryForObject("SELECT id FROM categories WHERE nome = ?", Long.class, name);
        jdbc.update("INSERT INTO transacao (descricao, valor, data, tipo, conta_id, categoria_id) VALUES (?, 9, '2026-09-01', 'RECEITA', ?, ?)",
                name, conta, categoria);
        long transacao = jdbc.queryForObject("SELECT id FROM transacao WHERE descricao = ?", Long.class, name);
        try (var client = new Client()) {
            String email = email(); client.register(email); client.login(email);
            assertEquals(0, json(client.call("GET", "/contas/listar", null, false), 200).size());
            assertEquals(0, json(client.call("GET", "/categorias/listar", null, false), 200).size());
            assertEquals(0, json(client.call("GET", "/transacoes/listar", null, false), 200).size());
            assertEquals(404, client.call("GET", "/contas/" + conta, null, false).statusCode());
            assertEquals(404, client.call("GET", "/categorias/" + categoria, null, false).statusCode());
            assertEquals(404, client.call("GET", "/transacoes/" + transacao, null, false).statusCode());
            assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM conta WHERE id = ?", Integer.class, conta));
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "TEST_FLYWAY_ENABLED", matches = "true")
    void postgresEnforcesCaseInsensitiveUniqueness() throws Exception {
        try (var client = new Client()) {
            String email = email(); client.register(email); client.login(email);
            client.create("/contas", Map.of("nome", "Carteira"));
            client.create("/categorias", Map.of("nome", "Alimentação"));
            long usuario = jdbc.queryForObject("SELECT id FROM usuarios WHERE email = ?", Long.class, email);
            assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
                    "INSERT INTO conta (nome, usuario_id) VALUES ('CARTEIRA', ?)", usuario));
            assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
                    "INSERT INTO categories (nome, usuario_id) VALUES ('ALIMENTAÇÃO', ?)", usuario));
            assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
                    "INSERT INTO usuarios (nome, email, senha_hash) VALUES ('Duplicado', ?, 'hash')", email.toUpperCase()));
        }
    }
}
