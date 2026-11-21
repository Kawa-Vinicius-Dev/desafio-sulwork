package com.sulwork.controller;

import com.sulwork.dto.MetaCategoriaRequest;
import com.sulwork.dto.MetaCategoriaResponse;
import com.sulwork.service.MetaCategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos/{eventoId}/metas")
public class MetaCategoriaController {

    private final MetaCategoriaService service;

    public MetaCategoriaController(MetaCategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<MetaCategoriaResponse> listar(@PathVariable Long eventoId) {
        return service.listar(eventoId);
    }

    @PutMapping("/{categoriaId}")
    public MetaCategoriaResponse definir(@PathVariable Long eventoId,
                                         @PathVariable Long categoriaId,
                                         @RequestBody @Valid MetaCategoriaRequest request) {
        return service.definir(eventoId, categoriaId, request);
    }

    @DeleteMapping("/{categoriaId}")
    public ResponseEntity<Void> remover(@PathVariable Long eventoId, @PathVariable Long categoriaId) {
        service.remover(eventoId, categoriaId);
        return ResponseEntity.noContent().build();
    }
}
