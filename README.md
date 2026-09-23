# Controle Financeiro

API REST de controle financeiro pessoal desenvolvida com Java, Spring Boot e PostgreSQL.

O projeto permite cadastrar usuários, gerenciar contas e categorias, registrar receitas e despesas e consultar saldo e extrato. Cada usuário acessa somente seus próprios dados.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Security
- Spring Data JPA / Hibernate
- Jakarta Bean Validation
- PostgreSQL 18
- Flyway
- Maven Wrapper
- JUnit e H2 para testes

## Funcionalidades

- Cadastro de usuários com senha protegida por BCrypt.
- Login por sessão, identificação do usuário autenticado e logout.
- Proteção CSRF nas operações que alteram dados.
- Gerenciamento de contas e categorias.
- Cadastro, consulta, atualização e exclusão de transações.
- Receitas e despesas com valores representados por BigDecimal.
- Cálculo de saldo por conta.
- Extrato com filtros opcionais de período e categoria.
- Bloqueio da exclusão de contas com transações.
- Verificação de nomes duplicados por usuário.
- Isolamento dos dados entre usuários.
- Respostas com DTOs, sem exposição de senhas ou hashes.
- Migrações versionadas do banco de dados.

## Requisitos

- JDK 21 configurado no JAVA_HOME.
- PostgreSQL 18 com banco de dados criado.
- Git.

Não é necessário instalar o Maven: o projeto inclui o Maven Wrapper.

## Executando localmente

Clone o repositório:

```bash
git clone https://github.com/victhor-guilherme/controle-financeiro.git
cd controle-financeiro
```

No PostgreSQL, crie o banco:

```sql
CREATE DATABASE "finance-control";
```

Configure as variáveis de ambiente. Exemplo no PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/finance-control"
$env:DB_USER = "seu_usuario"
$env:DB_PASSWORD = "sua_senha"
```

Inicie a aplicação:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS, use `./mvnw`.

No IntelliJ, configure as mesmas variáveis na configuração de execução de `FinanceControlApplication`.

A API estará disponível em:

```text
http://localhost:8080
```

Verifique o funcionamento com:

```http
GET /status
```

As migrações do Flyway são executadas na inicialização. O Hibernate valida o esquema resultante.

## Autenticação e CSRF

A autenticação utiliza sessão e cookie `JSESSIONID`.

O cliente precisa preservar os cookies entre as requisições. Nos exemplos, isso é feito pelo objeto de sessão do PowerShell.

Fluxo:

1. Consulte `GET /auth/csrf`.
2. Guarde o cookie e os campos `token` e `headerName` recebidos.
3. Envie o token no cabeçalho indicado ao cadastrar ou fazer login.
4. Após o login, consulte `/auth/csrf` novamente: o token anterior é invalidado.
5. Envie o cookie nas próximas chamadas e o token CSRF em POST, PUT e DELETE.
6. Para sair, envie `POST /auth/logout` com o token CSRF.

Exemplo no PowerShell, usando um e-mail ainda não cadastrado:

```powershell
$base = "http://localhost:8080"

# Obtém o token e guarda o cookie.
$csrf = Invoke-RestMethod "$base/auth/csrf" -SessionVariable sessao
$headers = @{}
$headers[$csrf.headerName] = $csrf.token

# Cadastro.
$cadastro = @{
    nome = "Pessoa Teste"
    email = "pessoa@example.com"
    senha = "MinhaSenha!123"
} | ConvertTo-Json

Invoke-RestMethod "$base/auth/registrar" `
    -Method Post `
    -WebSession $sessao `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $cadastro

# Login.
$login = @{
    email = "pessoa@example.com"
    senha = "MinhaSenha!123"
} | ConvertTo-Json

Invoke-RestMethod "$base/auth/login" `
    -Method Post `
    -WebSession $sessao `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $login

# Atualiza o token após o login.
$csrf = Invoke-RestMethod "$base/auth/csrf" -WebSession $sessao
$headers = @{}
$headers[$csrf.headerName] = $csrf.token

# Consulta o usuário autenticado.
Invoke-RestMethod "$base/auth/me" -WebSession $sessao

# Cria uma conta.
Invoke-RestMethod "$base/contas" `
    -Method Post `
    -WebSession $sessao `
    -Headers $headers `
    -ContentType "application/json" `
    -Body '{"nome":"Carteira"}'
```

No Postman ou Insomnia, preserve os cookies e envie o cabeçalho CSRF informado pela API.

## Endpoints

### Autenticação

| Método | Rota | Descrição |
|---|---|---|
| GET | `/status` | Verifica o funcionamento da API |
| GET | `/auth/csrf` | Obtém o token CSRF |
| POST | `/auth/registrar` | Cadastra um usuário |
| POST | `/auth/login` | Autentica e estabelece a sessão |
| GET | `/auth/me` | Consulta o usuário autenticado |
| POST | `/auth/logout` | Encerra a sessão |

### Contas

| Método | Rota | Descrição |
|---|---|---|
| POST | `/contas` | Cria uma conta |
| GET | `/contas/listar` | Lista as contas do usuário |
| GET | `/contas/{id}` | Consulta uma conta |
| PUT | `/contas/atualizar/{id}` | Atualiza o nome |
| DELETE | `/contas/excluir/{id}` | Exclui uma conta sem transações |
| GET | `/contas/{contaId}/saldo` | Consulta o saldo |
| GET | `/contas/{contaId}/extrato` | Consulta o extrato |

Filtros opcionais do extrato:

```text
?dataInicio=2026-09-01&dataFim=2026-09-30&categoriaId=1
```

As datas inicial e final são inclusivas.

### Categorias

| Método | Rota | Descrição |
|---|---|---|
| POST | `/categorias` | Cria uma categoria |
| GET | `/categorias/listar` | Lista as categorias do usuário |
| GET | `/categorias/{id}` | Consulta uma categoria |
| PUT | `/categorias/atualizar/{id}` | Atualiza o nome |

### Transações

| Método | Rota | Descrição |
|---|---|---|
| POST | `/contas/{contaId}/transacoes` | Registra uma transação |
| GET | `/transacoes/listar` | Lista as transações do usuário |
| GET | `/transacoes/{id}` | Consulta uma transação |
| PUT | `/transacoes/atualizar/{id}` | Atualiza uma transação |
| DELETE | `/transacoes/excluir/{id}` | Exclui uma transação |

Exemplo de corpo para criar ou atualizar uma transação:

```json
{
  "descricao": "Salário",
  "valor": 3500.00,
  "data": "2026-09-05",
  "tipo": "RECEITA",
  "categoriaId": 1
}
```

Use uma categoria pertencente ao usuário autenticado. Os tipos disponíveis são `RECEITA` e `DESPESA`.

## Regras e respostas

- Valores de transações devem ser positivos e ter até duas casas decimais.
- A senha de cadastro exige pelo menos oito caracteres e aceita até 72 bytes em UTF-8.
- E-mails são normalizados e não podem se repetir.
- Nomes de contas e categorias não podem se repetir para o mesmo usuário, ignorando maiúsculas.
- Uma transação deve usar conta e categoria do mesmo usuário.
- Recursos de outros usuários são tratados como não encontrados.
- Contas com transações não podem ser excluídas.

Principais status HTTP:

| Status | Significado |
|---|---|
| 200 | Operação ou consulta concluída |
| 201 | Usuário cadastrado |
| 204 | Exclusão ou logout concluído |
| 400 | Dados inválidos |
| 401 | Autenticação necessária ou credenciais inválidas |
| 403 | Acesso negado, incluindo falha de CSRF |
| 404 | Recurso não encontrado para o usuário |
| 409 | Duplicidade ou conflito de integridade |

## Testes

Execute:

```powershell
.\mvnw.cmd test
```

Por padrão, os testes usam H2 e não precisam das credenciais do PostgreSQL. O teste específico das restrições do PostgreSQL é ignorado nessa execução.

Para executar a suíte com as migrações reais, crie um banco exclusivo de testes no PostgreSQL 18 e configure:

```powershell
$env:TEST_DB_URL = "jdbc:postgresql://localhost:5432/finance_control_test"
$env:TEST_DB_USER = "seu_usuario"
$env:TEST_DB_PASSWORD = "sua_senha"
$env:TEST_FLYWAY_ENABLED = "true"
$env:TEST_DDL_AUTO = "validate"

.\mvnw.cmd verify
```

Use um banco exclusivo: a suíte cria registros de teste.

A cobertura inclui sessão, CSRF, logout, credenciais inválidas, isolamento entre usuários, operações financeiras, filtros e restrições de integridade.

## Banco de dados e registros antigos

O esquema é versionado pelo Flyway.

Na adoção de um banco criado pelas versões anteriores do projeto, contas e categorias sem proprietário são preservadas, mas não ficam acessíveis pela API autenticada. Para disponibilizá-las, é necessário atribuir explicitamente o usuário correto após revisar os registros.

As sessões ficam na memória da aplicação e expiram após 30 minutos de inatividade. Reiniciar a aplicação encerra as sessões, mas preserva os dados financeiros no PostgreSQL.

Para disponibilizar a aplicação por HTTPS, configure `SESSION_COOKIE_SECURE=true`.

## Licença

Distribuído sob a licença MIT. Consulte o arquivo [LICENSE](LICENSE).