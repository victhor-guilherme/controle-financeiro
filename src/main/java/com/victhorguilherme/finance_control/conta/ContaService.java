package com.victhorguilherme.finance_control.conta;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContaService {

    private List<Conta> contaList = new ArrayList<>();

    private long proximoId = 1;

    public Conta criarConta(String nome){
        Conta novaConta = new Conta(nome, proximoId);
        contaList.add(novaConta);
        proximoId++;
        return novaConta;
    }

    public List<Conta> listarContas(){
        List<Conta> copyContaList = new ArrayList<>();
        for(Conta contas: contaList){
            copyContaList.add(contas);
        }
        return copyContaList;
    }



}
