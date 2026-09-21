package com.victhorguilherme.finance_control.conta;
import com.victhorguilherme.finance_control.exceptions.NomeContaDuplicadoException;
import com.victhorguilherme.finance_control.exceptions.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContaService {

    private List<Conta> contaList;

    {
        contaList = new ArrayList<>();
    }

    private long proximoId = 1;

    public Conta criarConta(String nome){

        String nomeLimpo = nome.strip();
        List<Conta> todasContas = listarContas();

        boolean nomeJaExiste = todasContas.stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nomeLimpo));

        if (nomeJaExiste) {
            throw new NomeContaDuplicadoException("Já existe uma conta cadastrada com o nome: " + nomeLimpo);
        }


        Conta novaConta = new Conta(nomeLimpo, proximoId);
        contaList.add(novaConta);
        proximoId++;
        return novaConta;
    }

    public List<Conta> listarContas(){
        return new ArrayList<>(contaList);
    }

    public Conta buscarPorId(long id) {
        for (Conta conta : contaList) {
            if (conta.getId() == id) {
                return conta;
            }
        }
        throw new RecursoNaoEncontradoException("Conta de ID: " + id + " , não encontrada.");
    }

    public Conta atualizarConta(long id, String nome){
        Conta conta = buscarPorId(id);

        String nomeLimpo = nome.strip();
        List<Conta> todasContas = listarContas();

        boolean nomeJaExiste = todasContas.stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nomeLimpo) && c.getId() != id);

        if (nomeJaExiste) {
            throw new NomeContaDuplicadoException("Já existe uma conta cadastrada com o nome: " + nomeLimpo);
        }

        conta.setNome(nome.strip());
        return conta;
    }


}
