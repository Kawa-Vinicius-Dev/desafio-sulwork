package com.sulwork.controller;

import com.sulwork.dto.ParticipacaoRequest;
import com.sulwork.dto.ParticipacaoResponse;
import com.sulwork.service.ParticipacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/eventos/{eventoId}/participacoes")
public class ParticipacaoController {

    private final ParticipacaoService service;

    public ParticipacaoController(ParticipacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ParticipacaoResponse> inscrever(@PathVariable Long eventoId,
                                                          @RequestBody @Valid ParticipacaoRequest request) {
        ParticipacaoResponse criada = service.inscrever(eventoId, request);
        return ResponseEntity
                .created(URI.create("/api/eventos/" + eventoId + "/participacoes/" + criada.id()))
                .body(criada);
    }

    @GetMapping
    public List<ParticipacaoResponse> listar(@PathVariable Long eventoId) {
        return service.listarPorEvento(eventoId);
    }
}