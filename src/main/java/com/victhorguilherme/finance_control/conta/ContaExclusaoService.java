package com.victhorguilherme.finance_control.conta;

import com.victhorguilherme.finance_control.exceptions.AccountHasTransactionsException;
import com.victhorguilherme.finance_control.transacao.TransacaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContaExclusaoService {
    private final ContaService contaService;
    private final TransacaoService transacaoService;

    public ContaExclusaoService(ContaService contaService, TransacaoService transacaoService) {
        this.contaService = contaService;
        this.transacaoService = transacaoService;
    }

    @Transactional
    public void deletarConta(long id) {
        if (transacaoService.possuiTransacao(id)) {
            throw new AccountHasTransactionsException("A conta possui transações e não pode ser excluída.");
        }
        contaService.deletarConta(id);
    }
}
