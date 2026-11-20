package com.sulwork.controller;

import com.sulwork.dto.ItemRequest;
import com.sulwork.dto.ItemResponse;
import com.sulwork.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/eventos/{eventoId}/itens")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemResponse> adicionar(@PathVariable Long eventoId,
                                                  @RequestBody @Valid ItemRequest request) {
        ItemResponse criado = service.adicionar(eventoId, request);
        return ResponseEntity
                .created(URI.create("/api/eventos/" + eventoId + "/itens/" + criado.id()))
                .body(criado);
    }

    @GetMapping
    public List<ItemResponse> listar(@PathVariable Long eventoId) {
        return service.listarPorEvento(eventoId);
    }
}