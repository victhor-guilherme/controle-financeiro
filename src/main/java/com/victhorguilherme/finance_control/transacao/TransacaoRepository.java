package com.victhorguilherme.finance_control.transacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    public List<Transacao> findByConta_Id(Long conta_id);
    public boolean existsByConta_Id(Long conta_id);
}
