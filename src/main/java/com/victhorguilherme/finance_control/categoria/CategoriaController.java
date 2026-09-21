package com.victhorguilherme.finance_control.categoria;

import com.victhorguilherme.finance_control.categoria.dto.CategoriaRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("/categorias")
@RestController
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public Categoria criarCategoria(@RequestBody @Valid CategoriaRequest request){
        return categoriaService.criarCategoria(request.getNome());
    }

    @GetMapping("/listar")
    public List<Categoria> listarCategoria(){
        return categoriaService.listarCategoria();
    }

    @PutMapping("/atualizar/{id}")
    public Categoria atualizarCategoria(@PathVariable long id, @RequestBody @Valid CategoriaRequest request){
        return categoriaService.atualizarCategoria(request.getNome(), id);
    }

    @GetMapping("/{id}")
    public Categoria buscarPorId(@PathVariable long id){
        return categoriaService.buscarPorId(id);
    }

}
