package com.sulwork.dto;

import com.sulwork.domain.TipoEvento;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventoRequest(

        @NotBlank @Size(max = 120) String nome,
        @Size(max = 1000) String descricao,
        @NotNull TipoEvento tipo,
        @NotNull @Future LocalDateTime dataHora,
        @NotNull @Future LocalDateTime prazoAlteracoes
){}