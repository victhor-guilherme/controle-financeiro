package com.victhorguilherme.finance_control.auth;

import com.victhorguilherme.finance_control.auth.dto.LoginRequest;
import com.victhorguilherme.finance_control.auth.dto.RegisterRequest;
import com.victhorguilherme.finance_control.exceptions.EmailUserDuplicate;
import com.victhorguilherme.finance_control.exceptions.InvalidCredentialsException;
import com.victhorguilherme.finance_control.usuario.Usuario;
import com.victhorguilherme.finance_control.usuario.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public Usuario registrar(RegisterRequest request) {
        String nome = request.getNome().strip();
        String email = request.getEmail().strip().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailUserDuplicate("Este e-mail já foi cadastrado.");
        }

        if (request.getSenha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("A senha deve ter no máximo 72 bytes em UTF-8.");
        }
        return usuarioRepository.saveAndFlush(
                new Usuario(nome, email, passwordEncoder.encode(request.getSenha())));
    }

    public Authentication autenticar(LoginRequest request) {
        if (request.getSenha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new InvalidCredentialsException("E-mail ou senha inválidos.");
        }
        try {
            return authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                    request.getEmail().strip().toLowerCase(Locale.ROOT), request.getSenha()));
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("E-mail ou senha inválidos.");
        }
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuario(Authentication authentication) {
        return usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new InvalidCredentialsException("E-mail ou senha inválidos."));
    }
}
