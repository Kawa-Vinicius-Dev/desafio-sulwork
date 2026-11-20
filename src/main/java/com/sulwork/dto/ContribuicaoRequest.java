package com.sulwork.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ContribuicaoRequest(@NotNull Long participanteId, @NotNull @Positive Integer quantidade) {
}