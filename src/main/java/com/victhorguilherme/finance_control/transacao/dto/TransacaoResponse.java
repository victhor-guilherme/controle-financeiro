package com.victhorguilherme.finance_control.transacao.dto;

import com.victhorguilherme.finance_control.categoria.dto.CategoriaResponse;
import com.victhorguilherme.finance_control.conta.dto.ContaResponse;
import com.victhorguilherme.finance_control.transacao.TipoTransacao;
import com.victhorguilherme.finance_control.transacao.Transacao;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoResponse(Long id, String descricao,
                                BigDecimal valor,
                                LocalDate data,
                                TipoTransacao tipo,
                                ContaResponse conta,
                                CategoriaResponse categoria) {

    public static TransacaoResponse de(Transacao transacao) {

        return new TransacaoResponse(transacao.getId(),
                transacao.getDescricao(), transacao.getValor(),
                transacao.getData(), transacao.getTipo(),
                ContaResponse.de(transacao.getConta()),
                CategoriaResponse.de(transacao.getCategoria()));
    }
}
