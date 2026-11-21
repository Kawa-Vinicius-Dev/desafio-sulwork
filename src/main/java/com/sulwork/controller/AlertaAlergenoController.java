package com.sulwork.controller;

import com.sulwork.dto.AlertaAlergenoResponse;
import com.sulwork.service.AlertaAlergenoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eventos/{eventoId}/alertas-alergenos")
public class AlertaAlergenoController {

    private final AlertaAlergenoService service;

    public AlertaAlergenoController(AlertaAlergenoService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlertaAlergenoResponse> listar(@PathVariable Long eventoId) {
        return service.listar(eventoId);
    }
}
