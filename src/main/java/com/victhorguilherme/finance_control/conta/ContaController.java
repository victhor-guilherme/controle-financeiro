package com.victhorguilherme.finance_control.conta;


import com.victhorguilherme.finance_control.conta.dto.ContaRequest;
import com.victhorguilherme.finance_control.exceptions.NomeContaDuplicadoException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/listar")
    public List<Conta> listarContas(){
        return contaService.listarContas();

    }

    @PostMapping()
    public Conta criarConta(@RequestBody @Valid ContaRequest request){
        return contaService.criarConta(request.getNome());
    }

    @GetMapping("/{id}")
    public Conta buscarPorId(@PathVariable long id){
        return contaService.buscarPorId(id);
    }

    @PutMapping("atualizar/{id}")
    public Conta atualizarConta(@PathVariable long id, @RequestBody @Valid ContaRequest request) {
        return contaService.atualizarConta(id, request.getNome());
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> deletarConta(@PathVariable long id){
        contaService.deletarConta(id);
        return ResponseEntity.noContent().build();
    }
}
