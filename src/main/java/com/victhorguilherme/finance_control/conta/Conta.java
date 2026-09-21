package com.victhorguilherme.finance_control.conta;

public class Conta {

    private long id;
    private String nome;

    public Conta(String nome, long id){
        this.id = id;
        this.nome = nome;
    }

    public long getId(){
        return this.id;
    }

    public String getNome(){
        return this.nome;
    }

    public void setNome(String novoNome){
        this.nome = novoNome;
    }

}
