package com.sulwork.dto;

import jakarta.validation.constraints.NotNull;

public record ParticipacaoRequest(@NotNull Long participanteId) {
}