package com.sulwork.dto;

import com.sulwork.domain.Evento;
import com.sulwork.domain.StatusEvento;
import com.sulwork.domain.TipoEvento;

import java.time.LocalDateTime;

public record EventoResponse(
        Long id,
        String nome,
        String descricao,
        TipoEvento tipo,
        LocalDateTime dataHora,
        LocalDateTime prazoAlteracoes,
        StatusEvento status
) {
    public static EventoResponse from(Evento e) {
        return new EventoResponse(e.getId(), e.getNome(), e.getDescricao(), e.getTipo(),
                e.getDataHora(), e.getPrazoAlteracoes(), e.getStatus());
    }
}