package com.victhorguilherme.finance_control.conta;

import com.victhorguilherme.finance_control.auth.UsuarioAtual;
import com.victhorguilherme.finance_control.conta.dto.ContaResponse;
import com.victhorguilherme.finance_control.exceptions.AccountNameDuplicate;
import com.victhorguilherme.finance_control.exceptions.ResourceNotFound;
import com.victhorguilherme.finance_control.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContaService {
    private final ContaRepository contaRepository;
    private final UsuarioAtual usuarioAtual;

    public ContaService(ContaRepository contaRepository, UsuarioAtual usuarioAtual) {
        this.contaRepository = contaRepository;
        this.usuarioAtual = usuarioAtual;
    }

    @Transactional
    public ContaResponse criarConta(String nome) {
        Usuario usuario = usuarioAtual.obter();
        String nomeLimpo = nome.strip();
        if (contaRepository.existsByNomeIgnoreCaseAndUsuario_Id(nomeLimpo, usuario.getId())) {
            throw new AccountNameDuplicate("Já existe uma conta cadastrada com o nome: " + nomeLimpo);
        }
        return ContaResponse.de(contaRepository.saveAndFlush(new Conta(nomeLimpo, usuario)));
    }

    public List<ContaResponse> listarContas() {
        return contaRepository.findByUsuario_IdOrderById(usuarioAtual.obter().getId())
                .stream().map(ContaResponse::de).toList();
    }

    public Conta buscarPorId(long id) {
        return contaRepository.findByIdAndUsuario_Id(id, usuarioAtual.obter().getId())
                .orElseThrow(() -> new ResourceNotFound("Conta não encontrada."));
    }

    public ContaResponse buscarResposta(long id) {
        return ContaResponse.de(buscarPorId(id));
    }

    @Transactional
    public ContaResponse atualizarConta(long id, String nome) {
        Conta conta = buscarPorId(id);
        String nomeLimpo = nome.strip();
        if (contaRepository.existsByNomeIgnoreCaseAndUsuario_IdAndIdNot(
                nomeLimpo, usuarioAtual.obter().getId(), id)) {
            throw new AccountNameDuplicate("Já existe uma conta cadastrada com o nome: " + nomeLimpo);
        }
        conta.setNome(nomeLimpo);
        return ContaResponse.de(contaRepository.saveAndFlush(conta));
    }

    @Transactional
    public void deletarConta(long id) {
        contaRepository.delete(buscarPorId(id));
        contaRepository.flush();
    }
}
