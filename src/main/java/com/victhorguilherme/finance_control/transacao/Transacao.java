package com.victhorguilherme.finance_control.transacao;

import com.victhorguilherme.finance_control.categoria.Categoria;
import com.victhorguilherme.finance_control.conta.Conta;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transacao{

    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private TipoTransacao tipo;
    private Conta conta;
    private Categoria categoria;

    public Transacao(
            Long id,
            String descricao,
            BigDecimal valor,
            LocalDate data,
            TipoTransacao tipo,
            Conta conta,
            Categoria categoria
            ){

        this.id = id;
        this.descricao = descricao;
        this.data = data;
        this.tipo = tipo;
        this.conta = conta;
        this.categoria = categoria;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Conta getConta() {
        return conta;
    }

    public LocalDate getData() {
        return data;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }


}
