package com.victhorguilherme.finance_control.conta;


import com.victhorguilherme.finance_control.conta.dto.ContaRequest;
import com.victhorguilherme.finance_control.exceptions.NomeContaDuplicadoException;
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
    public Conta atualizarConta(long id, String nome) {
        List<Conta> todasContas = contaService.listarContas();

        boolean nomeJaExiste = todasContas.stream()
                .anyMatch(conta -> conta.getNome().equalsIgnoreCase(nome) && conta.getId() != id);

        if (nomeJaExiste) {
            throw new NomeContaDuplicadoException("Já existe uma conta cadastrada com o nome: " + nome);
        }

        Conta conta = buscarPorId(id);
        conta.setNome(nome);
        return conta;
    }




}
