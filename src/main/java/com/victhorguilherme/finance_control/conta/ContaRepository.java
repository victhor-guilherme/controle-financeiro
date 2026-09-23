package com.victhorguilherme.finance_control.conta;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findByUsuario_IdOrderById(Long usuarioId);
    Optional<Conta> findByIdAndUsuario_Id(Long id, Long usuarioId);
    boolean existsByNomeIgnoreCaseAndUsuario_Id(String nome, Long usuarioId);
    boolean existsByNomeIgnoreCaseAndUsuario_IdAndIdNot(String nome, Long usuarioId, Long id);
}
