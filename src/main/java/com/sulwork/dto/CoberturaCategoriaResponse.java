package com.sulwork.dto;

/**
 * Cobertura de uma categoria no evento.
 * meta é null quando o organizador não definiu meta para a categoria.
 */
public record CoberturaCategoriaResponse(
        Long categoriaId,
        String categoriaNome,
        int percentual,
        Integer meta,
        boolean metaAtingida
) {}
