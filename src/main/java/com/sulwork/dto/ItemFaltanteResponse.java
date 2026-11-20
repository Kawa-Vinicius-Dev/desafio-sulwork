package com.sulwork.dto;

import com.sulwork.repository.ItemFaltanteView;

public record ItemFaltanteResponse(Long itemId, String nome, String categoriaNome,
                                   Integer quantidadeNecessaria, Long quantidadeConfirmada,
                                   Long faltam, Boolean obrigatorio) {
    public static ItemFaltanteResponse from(ItemFaltanteView v) {
        return new ItemFaltanteResponse(v.getItemId(), v.getNome(), v.getCategoriaNome(),
                v.getQuantidadeNecessaria(), v.getQuantidadeConfirmada(),
                v.getQuantidadeNecessaria() - v.getQuantidadeConfirmada(), v.getObrigatorio());
    }
}