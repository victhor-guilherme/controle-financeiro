package com.victhorguilherme.finance_control.transacao;

import com.victhorguilherme.finance_control.categoria.Categoria;
import com.victhorguilherme.finance_control.categoria.CategoriaService;
import com.victhorguilherme.finance_control.conta.Conta;
import com.victhorguilherme.finance_control.conta.ContaService;
import com.victhorguilherme.finance_control.exceptions.InvalidPeriodException;
import com.victhorguilherme.finance_control.exceptions.ResourceNotFound;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransacaoService {

    private final ContaService contaService;
    private final CategoriaService categoriaService;
    private final TransacaoRepository transacaoRepository;


    public TransacaoService(ContaService contaService,
                            CategoriaService categoriaService,
                            TransacaoRepository transacaoRepository){

        this.contaService = contaService;
        this.categoriaService = categoriaService;
        this.transacaoRepository = transacaoRepository;
    }

    public Transacao criarTransacao(
                                    String descricao,
                                    BigDecimal valor,
                                    LocalDate data,
                                    TipoTransacao tipo,
                                    long contaId,
                                    long categoriaId) {

        Conta conta = contaService.buscarPorId(contaId);
        Categoria categoria = categoriaService.buscarPorId(categoriaId);

        Transacao transacao = new Transacao(
                descricao.strip(),
                valor,
                data,
                tipo,
                conta,
                categoria
        );

     return transacaoRepository.save(transacao);
    }

    public Transacao buscarPorId(long id){
        return transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Transação de ID: " + id + " , não encontrada."));
    }

    public List<Transacao> listarTransacoes() {
       return transacaoRepository.findAll();
    }

    public void deletarTransacao(long id){
        Transacao transacaoEncontrada = buscarPorId(id);
        transacaoRepository.deleteById(transacaoEncontrada.getId());
    }

    public Transacao atualizarTransacao(long id,
                                        String descricao,
                                        BigDecimal valor,
                                        LocalDate data,
                                        TipoTransacao tipo,
                                        long categoriaId) {

        Transacao transacao = buscarPorId(id);

        Categoria categoria = categoriaService.buscarPorId(categoriaId);

            String descricaoLimpa = descricao.strip();

            transacao.setDescricao(descricaoLimpa);
            transacao.setCategoria(categoria);
            transacao.setTipo(tipo);
            transacao.setData(data);
            transacao.setValor(valor);

            return transacaoRepository.save(transacao);
        }

        public BigDecimal calcularSaldo(long contaId) {
            Conta conta = contaService.buscarPorId(contaId);
            BigDecimal saldo = BigDecimal.ZERO;

            for (Transacao transacao : transacaoRepository.findByConta_Id(contaId)) {
                if (transacao.getTipo() == TipoTransacao.RECEITA) {
                        saldo = saldo.add(transacao.getValor());
                    } else if (transacao.getTipo() == TipoTransacao.DESPESA) {
                        saldo = saldo.subtract(transacao.getValor());
                    }
                }

            return saldo;
        }

        public List<Transacao> consultarExtrato(long contaId,
                                                LocalDate dataInicio,
                                                LocalDate dataFim,
                                                Long categoriaId){

            Conta contaEncontrada = contaService.buscarPorId(contaId);

            if(categoriaId !=null){
                Categoria categoriaEncontrada = categoriaService.buscarPorId(categoriaId);
            }

            if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)){
                throw new InvalidPeriodException("A data de ínicio não pode ser posterior à data fim");
            }


                List<Transacao> resultExtract = new ArrayList<>();
                   for(Transacao transacao : transacaoRepository.findByConta_Id(contaId)){

                        if(dataInicio != null && transacao.getData().isBefore(dataInicio)){
                            continue;
                        }

                        if(dataFim != null && transacao.getData().isAfter(dataFim)){
                            continue;

                        }

                        if(categoriaId != null && !categoriaId.equals(transacao.getCategoria().getId())){
                            continue;
                        }

                        resultExtract.add(transacao);
                    }

                return resultExtract;
            }


            public boolean possuiTransacao(long contaId){

                Conta contaEncontrada = contaService.buscarPorId(contaId);
                return transacaoRepository.existsByConta_Id(contaId);


            }
        }
