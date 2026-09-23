package com.victhorguilherme.finance_control.usuario;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, name = "email", unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    public Usuario (String nome, String email, String senhaHash){
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
    }

    protected Usuario(){

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

    public String getSenhaHash() {
        return senhaHash;
    }
}
