package com.victhorguilherme.finance_control.conta.dto;

import jakarta.validation.constraints.NotBlank;

public class ContaRequest {

    @NotBlank
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
