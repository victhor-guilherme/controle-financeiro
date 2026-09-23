package com.victhorguilherme.finance_control.categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByUsuario_IdOrderById(Long usuarioId);
    Optional<Categoria> findByIdAndUsuario_Id(Long id, Long usuarioId);
    boolean existsByNomeIgnoreCaseAndUsuario_Id(String nome, Long usuarioId);
    boolean existsByNomeIgnoreCaseAndUsuario_IdAndIdNot(String nome, Long usuarioId, Long id);
}
