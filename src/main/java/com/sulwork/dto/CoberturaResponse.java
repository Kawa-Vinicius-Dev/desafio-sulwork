package com.sulwork.dto;

import java.util.List;

/**
 * Resumo de quanto do evento já está garantido.
 * pronto = todos os obrigatórios completos e todas as metas atingidas.
 */
public record CoberturaResponse(
        int percentualGeral,
        boolean obrigatoriosCompletos,
        boolean pronto,
        List<CoberturaCategoriaResponse> categorias
) {}
