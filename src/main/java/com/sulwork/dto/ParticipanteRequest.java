package com.sulwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record ParticipanteRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @CPF(message = "CPF inválido") String cpf,
        @Size(max = 1000) String restricoesAlimentares
) {
}