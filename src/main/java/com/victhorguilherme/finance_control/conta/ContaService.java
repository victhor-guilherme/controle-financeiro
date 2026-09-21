package com.victhorguilherme.finance_control.conta;

import com.victhorguilherme.finance_control.exceptions.RecursoNaoEncontradoException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

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

    public Conta buscarPorId(@PathVariable long id) {
        for (Conta conta : contaList) {
            if (conta.getId() == id) {
                return conta;
            }
        }
        throw new RecursoNaoEncontradoException();
    }

    public Conta criar(@RequestBody Conta conta){
        return criarConta(conta.getNome());
    }

}
