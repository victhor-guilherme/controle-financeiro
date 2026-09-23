package com.victhorguilherme.finance_control.conta;
import com.victhorguilherme.finance_control.exceptions.AccountNameDuplicate;
import com.victhorguilherme.finance_control.exceptions.ResourceNotFound;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }


    public Conta criarConta(String nome) {

        String nomeLimpo = nome.strip();
        List<Conta> todasContas = listarContas();

        boolean nomeJaExiste = todasContas.stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nomeLimpo));

        if (nomeJaExiste) {
            throw new AccountNameDuplicate("Já existe uma conta cadastrada com o nome: " + nomeLimpo);
        }


        Conta novaConta = new Conta(nomeLimpo);
        contaRepository.save(novaConta);
        return novaConta;
    }

    public List<Conta> listarContas() {
        return contaRepository.findAll();
    }

    public Conta buscarPorId(long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Conta de ID: " + id + " , não encontrada."));

    return conta;
}

    public Conta atualizarConta(long id, String nome){
        Conta conta = buscarPorId(id);

        String nomeLimpo = nome.strip();
        List<Conta> todasContas = listarContas();

        boolean nomeJaExiste = todasContas.stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nomeLimpo) && c.getId() != id);

        if (nomeJaExiste) {
            throw new AccountNameDuplicate("Já existe uma conta cadastrada com o nome: " + nomeLimpo);
        }

        conta.setNome(nomeLimpo);
        contaRepository.save(conta);
        return conta;
    }

    public void deletarConta(long id){
        contaRepository.deleteById(id);
    }



}
