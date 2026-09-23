package com.victhorguilherme.finance_control.auth;

import com.victhorguilherme.finance_control.auth.dto.LoginRequest;
import com.victhorguilherme.finance_control.auth.dto.LoginResponse;
import com.victhorguilherme.finance_control.auth.dto.RegisterRequest;
import com.victhorguilherme.finance_control.auth.dto.RegisterResponse;
import com.victhorguilherme.finance_control.usuario.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final SecurityContextRepository contextRepository;
    private final SessionAuthenticationStrategy sessionStrategy;

    public AuthController(AuthService authService, SecurityContextRepository contextRepository,
                          SessionAuthenticationStrategy sessionStrategy) {
        this.authService = authService;
        this.contextRepository = contextRepository;
        this.sessionStrategy = sessionStrategy;
    }

    @GetMapping("/csrf")
    public CsrfResponse csrf(CsrfToken token) {
        return new CsrfResponse(token.getToken(), token.getHeaderName());
    }

    @PostMapping("/registrar")
    public ResponseEntity<RegisterResponse> registrar(@RequestBody @Valid RegisterRequest request) {
        Usuario usuario = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(usuario.getId(), usuario.getNome(), usuario.getEmail()));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest body,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        Authentication authentication = authService.autenticar(body);

        Usuario usuario = authService.buscarUsuario(authentication);

        sessionStrategy.onAuthentication(authentication, request, response);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        contextRepository.saveContext(context, request, response);

        return new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    @GetMapping("/me")
    public LoginResponse me(Authentication authentication) {
        Usuario usuario = authService.buscarUsuario(authentication);
        return new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    public record CsrfResponse(String token, String headerName) { }
}
