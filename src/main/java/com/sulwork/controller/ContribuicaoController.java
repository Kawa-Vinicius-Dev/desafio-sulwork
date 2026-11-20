package com.sulwork.controller;

import com.sulwork.dto.AlterarContribuicaoRequest;
import com.sulwork.dto.ContribuicaoRequest;
import com.sulwork.dto.ContribuicaoResponse;
import com.sulwork.dto.ItemFaltanteResponse;
import com.sulwork.service.ContribuicaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/eventos/{eventoId}")
public class ContribuicaoController {

    private final ContribuicaoService service;

    public ContribuicaoController(ContribuicaoService service) {
        this.service = service;
    }

    @PostMapping("/itens/{itemId}/contribuicoes")
    public ResponseEntity<ContribuicaoResponse> contribuir(@PathVariable Long eventoId,
                                                           @PathVariable Long itemId,
                                                           @RequestBody @Valid ContribuicaoRequest request) {
        ContribuicaoResponse criada = service.contribuir(eventoId, itemId, request);
        return ResponseEntity
                .created(URI.create("/api/eventos/" + eventoId + "/contribuicoes/" + criada.id()))
                .body(criada);
    }

    @PutMapping("/contribuicoes/{contribuicaoId}")
    public ContribuicaoResponse alterar(@PathVariable Long eventoId,
                                        @PathVariable Long contribuicaoId,
                                        @RequestBody @Valid AlterarContribuicaoRequest request) {
        return service.alterar(eventoId, contribuicaoId, request);
    }

    @DeleteMapping("/contribuicoes/{contribuicaoId}")
    public ResponseEntity<Void> remover(@PathVariable Long eventoId, @PathVariable Long contribuicaoId) {
        service.remover(eventoId, contribuicaoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/contribuicoes")
    public List<ContribuicaoResponse> listar(@PathVariable Long eventoId) {
        return service.listarPorEvento(eventoId);
    }

    @GetMapping("/faltantes")
    public List<ItemFaltanteResponse> faltantes(@PathVariable Long eventoId) {
        return service.faltantes(eventoId);
    }
}
