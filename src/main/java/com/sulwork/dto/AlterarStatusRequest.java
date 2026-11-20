package com.sulwork.dto;

import com.sulwork.domain.StatusEvento;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusRequest(@NotNull StatusEvento status) {
}
