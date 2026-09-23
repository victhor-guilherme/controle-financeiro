package com.victhorguilherme.finance_control.transacao.dto;

import com.victhorguilherme.finance_control.transacao.TipoTransacao;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransacaoRequest {

    @NotBlank
    private String descricao;

    @NotNull
    @Positive
    @Digits(integer = 17, fraction = 2)
    private BigDecimal valor;

    @NotNull
    private LocalDate data;

    @NotNull
    private TipoTransacao tipo;

    @NotNull
    @Positive
    private Long categoriaId;


    public TipoTransacao getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
