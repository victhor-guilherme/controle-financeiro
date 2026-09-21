package com.victhorguilherme.finance_control.conta;
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
        Conta novaConta = new Conta(nome, proximoId);
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
        conta.setNome(nome);
        return conta;
    }


}
