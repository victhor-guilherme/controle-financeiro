package com.victhorguilherme.finance_control.auth.dto;

public class RegisterResponse {
    private Long id;
    private String nome;
    private String email;

    public RegisterResponse(Long id, String nome ,String email){
        this.id = id;
        this.nome = nome;
        this.email = email;

    }

    public String getNome() {
        return nome;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
