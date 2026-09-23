package com.victhorguilherme.finance_control.categoria;

import com.victhorguilherme.finance_control.auth.UsuarioAtual;
import com.victhorguilherme.finance_control.categoria.dto.CategoriaResponse;
import com.victhorguilherme.finance_control.exceptions.CategoryNameDuplicate;
import com.victhorguilherme.finance_control.exceptions.ResourceNotFound;
import com.victhorguilherme.finance_control.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final UsuarioAtual usuarioAtual;

    public CategoriaService(CategoriaRepository categoriaRepository, UsuarioAtual usuarioAtual) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioAtual = usuarioAtual;
    }

    @Transactional
    public CategoriaResponse criarCategoria(String nome) {
        Usuario usuario = usuarioAtual.obter();
        String nomeLimpo = nome.strip();
        if (categoriaRepository.existsByNomeIgnoreCaseAndUsuario_Id(nomeLimpo, usuario.getId())) {
            throw new CategoryNameDuplicate("Já existe uma categoria cadastrada com este nome: " + nomeLimpo);
        }
        return CategoriaResponse.de(categoriaRepository.saveAndFlush(new Categoria(nomeLimpo, usuario)));
    }

    public List<CategoriaResponse> listarCategoria() {
        return categoriaRepository.findByUsuario_IdOrderById(usuarioAtual.obter().getId())
                .stream().map(CategoriaResponse::de).toList();
    }

    public Categoria buscarPorId(long id) {
        return categoriaRepository.findByIdAndUsuario_Id(id, usuarioAtual.obter().getId())
                .orElseThrow(() -> new ResourceNotFound("Categoria não encontrada."));
    }

    public CategoriaResponse buscarResposta(long id) {
        return CategoriaResponse.de(buscarPorId(id));
    }

    @Transactional
    public CategoriaResponse atualizarCategoria(String nome, long id) {
        Categoria categoria = buscarPorId(id);
        String nomeLimpo = nome.strip();
        if (categoriaRepository.existsByNomeIgnoreCaseAndUsuario_IdAndIdNot(
                nomeLimpo, usuarioAtual.obter().getId(), id)) {
            throw new CategoryNameDuplicate("Já existe uma categoria cadastrada com este nome: " + nomeLimpo);
        }
        categoria.setNome(nomeLimpo);
        return CategoriaResponse.de(categoriaRepository.saveAndFlush(categoria));
    }
}

