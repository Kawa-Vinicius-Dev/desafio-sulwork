package com.sulwork.dto;

import com.sulwork.domain.MetaCategoria;

public record MetaCategoriaResponse(
        Long id,
        Long eventoId,
        Long categoriaId,
        String categoriaNome,
        Integer percentual
) {
    public static MetaCategoriaResponse from(MetaCategoria m) {
        return new MetaCategoriaResponse(m.getId(), m.getEvento().getId(), m.getCategoria().getId(),
                m.getCategoria().getNome(), m.getPercentual());
    }
}
