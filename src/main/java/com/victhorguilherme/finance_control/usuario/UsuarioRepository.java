package com.victhorguilherme.finance_control.usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public Boolean existsByEmailIgnoreCase(String email);

}
