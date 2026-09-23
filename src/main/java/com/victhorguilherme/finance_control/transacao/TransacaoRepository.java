package com.victhorguilherme.finance_control.transacao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByConta_Usuario_IdOrderByDataAscIdAsc(Long usuarioId);

    Optional<Transacao> findByIdAndConta_Usuario_Id(Long id, Long usuarioId);

    List<Transacao> findByConta_IdAndConta_Usuario_IdOrderByDataAscIdAsc(Long contaId, Long usuarioId);

    boolean existsByConta_IdAndConta_Usuario_Id(Long contaId, Long usuarioId);
}
