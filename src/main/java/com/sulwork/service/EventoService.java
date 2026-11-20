package com.sulwork.service;

import com.sulwork.domain.Evento;
import com.sulwork.domain.StatusEvento;
import com.sulwork.dto.EventoRequest;
import com.sulwork.dto.EventoResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.exception.ResourceNotFoundException;
import com.sulwork.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
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

    @Transactional(readOnly = true)
    public List<EventoResponse> listar() {
        return repository.findAll().stream()
                .map(EventoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoResponse buscar(Long id) {
        return EventoResponse.from(buscarEntidade(id));
    }

    Evento buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }
    @Transactional
    public EventoResponse alterarStatus(Long id, StatusEvento novo) {

        Evento evento = buscarEntidade(id);
        if (!evento.getStatus().podeMudarPara(novo)) {
            throw new BusinessRuleException(
                    "Não é possivel mudar o status de "
                            + evento.getStatus()
                            + " para "
                            + novo
            );
        }
        evento.setStatus(novo);
        return EventoResponse.from(evento);
    }
}
