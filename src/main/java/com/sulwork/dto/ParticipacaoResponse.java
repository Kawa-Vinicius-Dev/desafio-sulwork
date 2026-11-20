package com.sulwork.dto;

import com.sulwork.domain.Participacao;

import java.time.LocalDateTime;

public record ParticipacaoResponse(Long id, Long eventoId, Long participanteId,
                                   String participanteNome, LocalDateTime inscritoEm) {
    public static ParticipacaoResponse from(Participacao p) {
        return new ParticipacaoResponse(p.getId(), p.getEvento().getId(),
                p.getParticipante().getId(), p.getParticipante().getNome(), p.getInscritoEm());
    }
}