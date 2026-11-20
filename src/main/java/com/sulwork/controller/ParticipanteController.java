package com.sulwork.controller;

import com.sulwork.dto.ParticipanteRequest;
import com.sulwork.dto.ParticipanteResponse;
import com.sulwork.service.ParticipanteService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    private final ParticipanteService service;

    public ParticipanteController(ParticipanteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ParticipanteResponse> criar(@RequestBody @Valid ParticipanteRequest request) {
        ParticipanteResponse criado = service.criar(request);
        return ResponseEntity.created(URI.create("/api/participantes/" + criado.id())).body(criado);
    }

    @GetMapping
    public List<ParticipanteResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ParticipanteResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }
}
