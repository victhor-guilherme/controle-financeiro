package com.victhorguilherme.finance_control.transacao;

import com.victhorguilherme.finance_control.transacao.dto.TransacaoRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService){
        this.transacaoService = transacaoService;
    }

    @PostMapping("/contas/{contaId}/transacoes")
    public Transacao criarTransacao(@PathVariable long contaId, @RequestBody @Valid TransacaoRequest request){
        return transacaoService.criarTransacao(
                request.getDescricao(),
                request.getValor(),
                request.getData(),
                request.getTipo(),
                contaId,
                request.getCategoriaId());
    }



}
