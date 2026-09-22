package com.victhorguilherme.finance_control.transacao;

import com.victhorguilherme.finance_control.transacao.dto.TransacaoRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/transacoes/{id}")
    public Transacao buscarPorId(@PathVariable long id){
        return transacaoService.buscarPorId(id);
    }

    @GetMapping("/transacoes/listar")
    public List<Transacao> listarTransacao(){
        return transacaoService.listarTransacoes();
    }

    @DeleteMapping("/transacoes/excluir/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable long id){
        transacaoService.deletarTransacao(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/transacoes/atualizar/{id}")
    public Transacao atualizarTransacao(@PathVariable long id,
                                        @RequestBody @Valid TransacaoRequest request){

        return transacaoService.atualizarTransacao(id,
                request.getDescricao(),
                request.getValor(),
                request.getData(),
                request.getTipo(),
                request.getCategoriaId());
    }

    @GetMapping("/contas/{contaId}/saldo")
    public BigDecimal consultarSaldo(@PathVariable long contaId){
        return transacaoService.calcularSaldo(contaId);
    }

    @GetMapping("/contas/{contaId}/extrato")
    public List<Transacao> consultarExtrato(@PathVariable long contaId,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim ){
        return transacaoService.consultarExtrato(contaId, dataInicio, dataFim);
    }


}
