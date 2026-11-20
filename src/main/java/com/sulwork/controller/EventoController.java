package com.sulwork.controller;

import com.sulwork.dto.EventoRequest;
import com.sulwork.dto.EventoResponse;
import com.sulwork.service.EventoService;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eventos")

public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service){
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoResponse criar(@RequestBody @Valid EventoRequest req){
        return service.criar(req);
    }
}
