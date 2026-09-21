package com.victhorguilherme.finance_control.categoria;

import com.victhorguilherme.finance_control.exceptions.CategoryNameDuplicate;
import com.victhorguilherme.finance_control.exceptions.ResourceNotFound;
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
        List<Categoria> todasCategorias = listarCategoria();

        boolean categoriaJaExiste = todasCategorias.stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nomeLimpo));

        if (categoriaJaExiste) {
            throw new CategoryNameDuplicate("Já existe uma categoria cadastrada com este nome: " + nomeLimpo);
        }

        Categoria categoria = new Categoria(contadorId++, nomeLimpo);
        categoriaList.add(categoria);
        return categoria;
    }

    public List<Categoria> listarCategoria(){
        return new ArrayList<>(categoriaList);
    }

    public Categoria buscarPorId(long id){
        for(Categoria categoria : categoriaList){
            if(categoria.getId() == id){
                return categoria;
            }
        }
        throw new ResourceNotFound("Categoria de ID: " + id + " , não encontrada.");
    }

    public Categoria atualizarCategoria(String nome, long id){
        Categoria categoria = buscarPorId(id);

        String nomeLimpo = nome.strip();
        List<Categoria> todasCategorias = listarCategoria();

        boolean categoriaJaExiste = todasCategorias.stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nomeLimpo) && c.getId() != id);


        if(categoriaJaExiste){
            throw new CategoryNameDuplicate("Já existe uma categoria cadastrada com este nome: " + nomeLimpo);
        }

        categoria.setNome(nome.strip());
        return categoria;
    }
}

