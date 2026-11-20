package com.sulwork.controller;

import com.sulwork.dto.EventoRequest;
import com.sulwork.dto.EventoResponse;
import com.sulwork.service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")

public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoResponse criar(@RequestBody @Valid EventoRequest req) {
        return service.criar(req);
    }

    @GetMapping
    public List<EventoResponse> listar(){
        return service.listar();
    }

    @GetMapping("/{id}")
    public EventoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }
}
