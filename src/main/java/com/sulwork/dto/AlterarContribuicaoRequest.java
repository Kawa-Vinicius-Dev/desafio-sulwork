package com.sulwork.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AlterarContribuicaoRequest(
        @NotNull @Positive Integer quantidade
) {}
