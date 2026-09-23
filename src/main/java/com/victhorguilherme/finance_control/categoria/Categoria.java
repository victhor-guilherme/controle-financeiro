package com.victhorguilherme.finance_control.categoria;

import jakarta.persistence.*;


@Entity
@Table(name = "categories")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    public Categoria(long id, String nome){
        this.id = id;
        this.nome = nome;
    }

    protected Categoria(){

    }

    public Categoria(String nome){
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public Long getId() {
        return id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
