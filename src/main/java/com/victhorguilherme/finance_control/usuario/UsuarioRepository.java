package com.victhorguilherme.finance_control.usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public Boolean existsByEmailIgnoreCase(String email);

    public Optional<Usuario> findByEmailIgnoreCase(String email);
}
