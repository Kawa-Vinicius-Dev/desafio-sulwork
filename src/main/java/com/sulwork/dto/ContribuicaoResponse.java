package com.sulwork.dto;

import com.sulwork.domain.Contribuicao;

import java.time.LocalDateTime;

public record ContribuicaoResponse(Long id, Long itemId, String itemNome, Long participanteId,
                                   String participanteNome, Integer quantidade, LocalDateTime criadoEm) {
    public static ContribuicaoResponse from(Contribuicao c) {
        return new ContribuicaoResponse(c.getId(), c.getItem().getId(), c.getItem().getNome(),
                c.getParticipante().getId(), c.getParticipante().getNome(),
                c.getQuantidade(), c.getCriadoEm());
    }
}