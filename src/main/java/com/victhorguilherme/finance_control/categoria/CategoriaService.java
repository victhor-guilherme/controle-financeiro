package com.victhorguilherme.finance_control.categoria;

import com.victhorguilherme.finance_control.exceptions.CategoryNameDuplicate;
import com.victhorguilherme.finance_control.exceptions.ResourceNotFound;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {


    private final CategoriaRepository categoriaRepository;



    public CategoriaService(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }


    public Categoria criarCategoria(String nome){
        String nomeLimpo = nome.strip();
        List<Categoria> todasCategorias = listarCategoria();

        boolean categoriaJaExiste = todasCategorias.stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nomeLimpo));

        if (categoriaJaExiste) {
            throw new CategoryNameDuplicate("Já existe uma categoria cadastrada com este nome: " + nomeLimpo);
        }

        Categoria categoria = new Categoria(nomeLimpo);
        categoriaRepository.save(categoria);
        return categoria;
    }

    public List<Categoria> listarCategoria(){
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(long id){

      Categoria categoria = categoriaRepository.findById(id)
              .orElseThrow(() -> new ResourceNotFound("Categoria de ID: " + id + " , não encontrada."));

      return categoria;
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

        categoria.setNome(nomeLimpo);
        categoriaRepository.save(categoria);
        return categoria;
    }
}

