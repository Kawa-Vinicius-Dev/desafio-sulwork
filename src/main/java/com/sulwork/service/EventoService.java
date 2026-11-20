package com.sulwork.service;

import com.sulwork.domain.Evento;
import com.sulwork.dto.EventoRequest;
import com.sulwork.dto.EventoResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.repository.EventoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class EventoService {

    private final EventoRepository repository;

    public EventoService(EventoRepository repository){
        this.repository = repository;
    }

    @Transactional
    public EventoResponse criar(EventoRequest req) {
        if (!req.prazoAlteracoes().isBefore(req.dataHora())) {
            throw new BusinessRuleException("O prazo de alterações deve " +
                    "ser antes da data do evento");
        }
        Evento evento = new Evento();
        evento.setNome(req.nome());
        evento.setDescricao(req.descricao());
        evento.setTipo(req.tipo());
        evento.setDataHora(req.dataHora());
        evento.setPrazoAlteracoes(req.prazoAlteracoes());
        return EventoResponse.from(repository.save(evento));

    }
}
