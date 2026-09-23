package com.victhorguilherme.finance_control.auth;

import com.victhorguilherme.finance_control.auth.dto.RegisterRequest;
import com.victhorguilherme.finance_control.exceptions.EmailUserDuplicate;
import com.victhorguilherme.finance_control.usuario.Usuario;
import com.victhorguilherme.finance_control.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder){

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(RegisterRequest registerRequest){

        String nome = registerRequest.getNome().strip();
        String email = registerRequest.getEmail().strip().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailUserDuplicate("Este e-mail já existe e já foi cadastrado.");
        }


        String senha = passwordEncoder.encode(registerRequest.getSenha());

        Usuario usuario = new Usuario(nome, email, senha);
        return usuarioRepository.save(usuario);


    }
}
