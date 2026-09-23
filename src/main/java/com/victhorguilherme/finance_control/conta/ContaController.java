package com.victhorguilherme.finance_control.conta;


import com.victhorguilherme.finance_control.conta.dto.ContaRequest;
import com.victhorguilherme.finance_control.conta.dto.ContaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;
    private final ContaExclusaoService contaExclusaoService;

    public ContaController(ContaService contaService,
                           ContaExclusaoService contaExclusaoService) {

        this.contaService = contaService;
        this.contaExclusaoService = contaExclusaoService;
    }

    @GetMapping("/listar")
    public List<ContaResponse> listarContas(){
        return contaService.listarContas();

    }

    @PostMapping()
    public ContaResponse criarConta(@RequestBody @Valid ContaRequest request){
        return contaService.criarConta(request.getNome());
    }

    @GetMapping("/{id}")
    public ContaResponse buscarPorId(@PathVariable long id){
        return contaService.buscarResposta(id);
    }

    @PutMapping("atualizar/{id}")
    public ContaResponse atualizarConta(@PathVariable long id, @RequestBody @Valid ContaRequest request) {
        return contaService.atualizarConta(id, request.getNome());
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> deletarConta(@PathVariable long id){
        contaExclusaoService.deletarConta(id);
        return ResponseEntity.noContent().build();
    }
}
