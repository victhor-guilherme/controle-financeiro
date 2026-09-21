package com.victhorguilherme.finance_control.categoria;

public class Categoria {

    private long id;
    private String nome;

    public Categoria(long id, String nome){
        this.id = id;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public long getId() {
        return id;
    }
}
