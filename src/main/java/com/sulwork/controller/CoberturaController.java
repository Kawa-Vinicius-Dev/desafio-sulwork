package com.sulwork.controller;

import com.sulwork.dto.CoberturaResponse;
import com.sulwork.service.CoberturaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eventos/{eventoId}/cobertura")
public class CoberturaController {

    private final CoberturaService service;

    public CoberturaController(CoberturaService service) {
        this.service = service;
    }

    @GetMapping
    public CoberturaResponse calcular(@PathVariable Long eventoId) {
        return service.calcular(eventoId);
    }
}
