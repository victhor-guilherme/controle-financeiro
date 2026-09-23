package com.victhorguilherme.finance_control.categoria.dto;

import com.victhorguilherme.finance_control.categoria.Categoria;

public record CategoriaResponse(Long id, String nome) {
    public static CategoriaResponse de(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }
}
