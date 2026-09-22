package com.victhorguilherme.finance_control.conta;

import com.victhorguilherme.finance_control.exceptions.AccountHasTransactionsException;
import com.victhorguilherme.finance_control.transacao.TransacaoService;
import org.springframework.stereotype.Service;

@Service
public class ContaExclusaoService {

    private final ContaService contaService;
    private final TransacaoService transacaoService;

    public ContaExclusaoService(ContaService contaService, TransacaoService transacaoService){
        this.contaService = contaService;
        this.transacaoService = transacaoService;
    }

    public void deletarConta(long id){
        if(transacaoService.possuiTransacao(id)){
            throw new AccountHasTransactionsException("A conta: " + id + " possui transação, não é possível exclui-lá.");
        }
        contaService.deletarConta(id);
    }





}
