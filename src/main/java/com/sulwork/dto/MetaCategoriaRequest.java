package com.sulwork.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MetaCategoriaRequest(
        @NotNull @Min(1) @Max(100) Integer percentual
) {}
