package com.sulwork.dto;

import com.sulwork.domain.Categoria;

public record CategoriaResponse(Long id, String nome) {
    public static CategoriaResponse from(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome());
    }
}