package com.victhorguilherme.finance_control.transacao;

import com.victhorguilherme.finance_control.auth.UsuarioAtual;
import com.victhorguilherme.finance_control.categoria.Categoria;
import com.victhorguilherme.finance_control.categoria.CategoriaService;
import com.victhorguilherme.finance_control.conta.Conta;
import com.victhorguilherme.finance_control.conta.ContaService;
import com.victhorguilherme.finance_control.exceptions.InvalidPeriodException;
import com.victhorguilherme.finance_control.exceptions.ResourceNotFound;
import com.victhorguilherme.finance_control.transacao.dto.TransacaoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TransacaoService {
    private final ContaService contaService;
    private final CategoriaService categoriaService;
    private final TransacaoRepository transacaoRepository;
    private final UsuarioAtual usuarioAtual;

    public TransacaoService(ContaService contaService, CategoriaService categoriaService,
                            TransacaoRepository transacaoRepository, UsuarioAtual usuarioAtual) {
        this.contaService = contaService;
        this.categoriaService = categoriaService;
        this.transacaoRepository = transacaoRepository;
        this.usuarioAtual = usuarioAtual;
    }

    @Transactional
    public TransacaoResponse criarTransacao(String descricao, BigDecimal valor, LocalDate data,
                                            TipoTransacao tipo, long contaId, long categoriaId) {
        Conta conta = contaService.buscarPorId(contaId);
        Categoria categoria = categoriaService.buscarPorId(categoriaId);
        Transacao transacao = new Transacao(descricao.strip(), valor, data, tipo, conta, categoria);
        return TransacaoResponse.de(transacaoRepository.saveAndFlush(transacao));
    }

    private Transacao buscarEntidade(long id) {
        return transacaoRepository.findByIdAndConta_Usuario_Id(id, usuarioAtual.obter().getId())
                .orElseThrow(() -> new ResourceNotFound("Transação não encontrada."));
    }

    public TransacaoResponse buscarPorId(long id) {
        return TransacaoResponse.de(buscarEntidade(id));
    }

    public List<TransacaoResponse> listarTransacoes() {
        return transacaoRepository.findByConta_Usuario_IdOrderByDataAscIdAsc(usuarioAtual.obter().getId())
                .stream().map(TransacaoResponse::de).toList();
    }

    @Transactional
    public void deletarTransacao(long id) {
        transacaoRepository.delete(buscarEntidade(id));
        transacaoRepository.flush();
    }

    @Transactional
    public TransacaoResponse atualizarTransacao(long id, String descricao, BigDecimal valor,
                                                LocalDate data, TipoTransacao tipo, long categoriaId) {
        Transacao transacao = buscarEntidade(id);
        Categoria categoria = categoriaService.buscarPorId(categoriaId);
        transacao.setDescricao(descricao.strip());
        transacao.setCategoria(categoria);
        transacao.setTipo(tipo);
        transacao.setData(data);
        transacao.setValor(valor);
        return TransacaoResponse.de(transacaoRepository.saveAndFlush(transacao));
    }

    public BigDecimal calcularSaldo(long contaId) {
        contaService.buscarPorId(contaId);
        return transacaoRepository.findByConta_IdAndConta_Usuario_IdOrderByDataAscIdAsc(
                        contaId, usuarioAtual.obter().getId()).stream()
                .map(transacao -> transacao.getTipo() == TipoTransacao.RECEITA
                        ? transacao.getValor() : transacao.getValor().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<TransacaoResponse> consultarExtrato(long contaId, LocalDate dataInicio,
                                                   LocalDate dataFim, Long categoriaId) {
        contaService.buscarPorId(contaId);
        if (categoriaId != null) {
            categoriaService.buscarPorId(categoriaId);
        }
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new InvalidPeriodException("A data de início não pode ser posterior à data fim.");
        }
        return transacaoRepository.findByConta_IdAndConta_Usuario_IdOrderByDataAscIdAsc(
                        contaId, usuarioAtual.obter().getId()).stream()
                .filter(t -> dataInicio == null || !t.getData().isBefore(dataInicio))
                .filter(t -> dataFim == null || !t.getData().isAfter(dataFim))
                .filter(t -> categoriaId == null || categoriaId.equals(t.getCategoria().getId()))
                .map(TransacaoResponse::de).toList();
    }

    public boolean possuiTransacao(long contaId) {
        contaService.buscarPorId(contaId);
        return transacaoRepository.existsByConta_IdAndConta_Usuario_Id(contaId, usuarioAtual.obter().getId());
    }
}
