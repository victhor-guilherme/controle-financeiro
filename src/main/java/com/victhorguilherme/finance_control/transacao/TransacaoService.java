package com.victhorguilherme.finance_control.transacao;

import com.victhorguilherme.finance_control.categoria.Categoria;
import com.victhorguilherme.finance_control.categoria.CategoriaService;
import com.victhorguilherme.finance_control.conta.Conta;
import com.victhorguilherme.finance_control.conta.ContaService;
import com.victhorguilherme.finance_control.exceptions.ResourceNotFound;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransacaoService {

    private List<Transacao> transacaoList = new ArrayList<>();

    private final ContaService contaService;
    private final CategoriaService categoriaService;
    private long contadorId = 1;

    public TransacaoService(ContaService contaService,
                            CategoriaService categoriaService){

        this.contaService = contaService;
        this.categoriaService = categoriaService;
    }

    public Transacao criarTransacao(
                                    String descricao,
                                    BigDecimal valor,
                                    LocalDate data,
                                    TipoTransacao tipo,
                                    long contaId,
                                    long categoriaId){

        Conta conta = contaService.buscarPorId(contaId);
        Categoria categoria = categoriaService.buscarPorId(categoriaId);

     Transacao transacao = new Transacao(
             contadorId++,
             descricao.strip(),
             valor,
             data,
             tipo,
             conta,
             categoria
     );


     transacaoList.add(transacao);
     return transacao;
    }

    public Transacao buscarPorId(long id){
        for (Transacao transacao : transacaoList) {
            if (transacao.getId() == id) {
                return transacao;
            }
        }
        throw new ResourceNotFound("Transação de ID: " + id + " , não encontrada.");
    }

    public List<Transacao> listarTransacoes() {
        return new ArrayList<>(transacaoList);

    }

    public void deletarTransacao(long id){
        Transacao transacaoEncontrada = buscarPorId(id);
        transacaoList.remove(transacaoEncontrada);
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

            return transacao;
        }

        public BigDecimal calcularSaldo(long contaId) {
            Conta conta = contaService.buscarPorId(contaId);
            BigDecimal saldo = BigDecimal.ZERO;

            for (Transacao transacao : transacaoList) {
                if (transacao.getConta().getId() == conta.getId()) {
                    if (transacao.getTipo() == TipoTransacao.RECEITA) {
                        saldo = saldo.add(transacao.getValor());
                    } else if (transacao.getTipo() == TipoTransacao.DESPESA) {
                        saldo = saldo.subtract(transacao.getValor());
                    }
                }
            }
            return saldo;
        }

        public List<Transacao> consultarExtrato(long contaId){
            Conta contaEncontrada = contaService.buscarPorId(contaId);

                List<Transacao> resultExtract = new ArrayList<>();
                for (Transacao transacao : transacaoList) {
                    if (contaEncontrada.getId() == transacao.getConta().getId()){
                        resultExtract.add(transacao);
                    }
                }
                return resultExtract;
            }

        }
