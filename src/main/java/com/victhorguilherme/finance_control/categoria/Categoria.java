package com.victhorguilherme.finance_control.categoria;

import com.victhorguilherme.finance_control.usuario.Usuario;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "categories")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", updatable = false)
    private Usuario usuario;

    protected Categoria() { }

    public Categoria(String nome, Usuario usuario) {
        this.nome = nome;
        this.usuario = Objects.requireNonNull(usuario);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Usuario getUsuario() { return usuario; }
    public void setNome(String nome) { this.nome = nome; }
}
