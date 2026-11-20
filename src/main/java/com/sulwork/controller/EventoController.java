package com.sulwork.controller;

import com.sulwork.dto.EventoRequest;
import com.sulwork.dto.EventoResponse;
import com.sulwork.service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")

public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> criar(@RequestBody @Valid EventoRequest req) {
        EventoResponse criado = service.criar(req);
        URI uri = URI.create("/api/eventos/" + criado.id());
        return ResponseEntity.created(uri).body(criado);
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
