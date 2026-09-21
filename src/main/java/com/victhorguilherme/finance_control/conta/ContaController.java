package com.victhorguilherme.finance_control.conta;


import com.victhorguilherme.finance_control.conta.dto.ContaRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/{id}")
    public List<Conta> listarContas(){
        return contaService.listarContas();

    }

    @PostMapping("/{id}")
    public Conta criarConta(@RequestBody @Valid ContaRequest request){
        return contaService.criarConta(request.getNome());
    }

    @GetMapping("/{id}")
    public Conta buscarPorId(@PathVariable long id){
        return contaService.buscarPorId(id);
    }




}
