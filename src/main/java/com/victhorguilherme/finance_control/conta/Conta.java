package com.victhorguilherme.finance_control.conta;

import jakarta.persistence.*;

@Entity
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    public Conta(String nome){
        this.nome = nome;
    }

    protected Conta(){

    }

    public Long getId(){
        return this.id;
    }

    public String getNome(){
        return this.nome;
    }

    public Conta setNome(String nome) {
        this.nome = nome;
        return this;
    }

}
