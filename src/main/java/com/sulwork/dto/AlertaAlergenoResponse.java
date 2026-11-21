package com.sulwork.dto;

import java.util.List;

/** Um participante inscrito que tem restrição a algum alérgeno de um item do evento. */
public record AlertaAlergenoResponse(
        Long itemId,
        String itemNome,
        Long participanteId,
        String participanteNome,
        List<String> alergenos
) {}
