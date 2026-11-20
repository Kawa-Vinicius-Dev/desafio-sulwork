package com.sulwork.dto;

import com.sulwork.domain.Participante;

public record ParticipanteResponse(Long id, String nome, String cpf, String restricoesAlimentares) {
    public static ParticipanteResponse from(Participante p) {
        return new ParticipanteResponse(p.getId(), p.getNome(), p.getCpf(), p.getRestricoesAlimentares());
    }
}
