package com.sulwork.dto;

import com.sulwork.domain.Item;

public record ItemResponse(Long id, Long eventoId, Long categoriaId, String categoriaNome,
                           String nome, Integer quantidadeNecessaria,
                           boolean obrigatorio, String alergenos) {
    public static ItemResponse from(Item i) {
        return new ItemResponse(i.getId(), i.getEvento().getId(),
                i.getCategoria().getId(), i.getCategoria().getNome(),
                i.getNome(), i.getQuantidadeNecessaria(),
                i.isObrigatorio(), i.getAlergenos());
    }
}