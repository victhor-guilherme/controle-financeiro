package com.victhorguilherme.finance_control.conta.dto;

import com.victhorguilherme.finance_control.conta.Conta;

public record ContaResponse(Long id, String nome) {
    public static ContaResponse de(Conta conta) {
        return new ContaResponse(conta.getId(), conta.getNome());
    }
}
