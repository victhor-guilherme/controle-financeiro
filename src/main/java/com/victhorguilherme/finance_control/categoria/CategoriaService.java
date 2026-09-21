package com.victhorguilherme.finance_control.categoria;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoriaService {

    private List<Categoria> categoriaList;
    {
        categoriaList = new ArrayList<>();
    }

    private long contadorId = 1;

    public Categoria criarCategoria(String nome){
        String nomeLimpo = nome.strip();
        Categoria categoria = new Categoria(contadorId++, nomeLimpo);
        categoriaList.add(categoria);
        return categoria;
    }

    public List<Categoria> listarCategoria(){
        return new ArrayList<>(categoriaList);
    }
}
