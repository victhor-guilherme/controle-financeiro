package com.victhorguilherme.finance_control.auth;

import com.victhorguilherme.finance_control.auth.dto.LoginRequest;
import com.victhorguilherme.finance_control.auth.dto.LoginResponse;
import com.victhorguilherme.finance_control.auth.dto.RegisterRequest;
import com.victhorguilherme.finance_control.auth.dto.RegisterResponse;
import com.victhorguilherme.finance_control.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<RegisterResponse> registrar(@RequestBody @Valid RegisterRequest request){
        Usuario usuario = authService.registrar(request);
       RegisterResponse response = new RegisterResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request){
        Usuario usuario = authService.autenticar(request);
        LoginResponse response = new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }





}
