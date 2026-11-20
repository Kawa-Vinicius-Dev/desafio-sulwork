package com.sulwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ItemRequest(
        @NotNull Long categoriaId,
        @NotBlank @Size(max = 120) String nome,
        @NotNull @Positive Integer quantidadeNecessaria,
        @NotNull Boolean obrigatorio,
        @Size(max = 500) String alergenos
) {
}