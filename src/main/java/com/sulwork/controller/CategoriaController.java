package com.sulwork.controller;

import com.sulwork.dto.CategoriaRequest;
import com.sulwork.dto.CategoriaResponse;
import com.sulwork.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@RequestBody @Valid CategoriaRequest request) {
        CategoriaResponse criada = service.criar(request);
        return ResponseEntity.created(URI.create("/api/categorias/" + criada.id())).body(criada);
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return service.listar();
    }
}