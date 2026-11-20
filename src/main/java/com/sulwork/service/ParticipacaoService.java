package com.sulwork.service;

import com.sulwork.domain.Evento;
import com.sulwork.domain.Participacao;
import com.sulwork.domain.Participante;
import com.sulwork.domain.StatusEvento;
import com.sulwork.dto.ParticipacaoRequest;
import com.sulwork.dto.ParticipacaoResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.repository.ParticipacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParticipacaoService {

    private final ParticipacaoRepository repository;
    private final EventoService eventoService;
    private final ParticipanteService participanteService;

    public ParticipacaoService(
            ParticipacaoRepository repository,
            EventoService eventoService,
            ParticipanteService participanteService) {

        this.repository = repository;
        this.eventoService = eventoService;
        this.participanteService = participanteService;

    }

    @Transactional
    public ParticipacaoResponse inscrever(
            Long eventoId,
            ParticipacaoRequest request) {

        Evento evento = eventoService.buscarEntidade(eventoId);
        Participante participante = participanteService.buscarEntidade(request.participanteId());

        if (evento.getStatus() != StatusEvento.ABERTO) {
            throw new BusinessRuleException("Inscrições só são permitidas com o evento ABERTO");
        }
        if (repository.existsByEventoIdAndParticipanteId(eventoId, participante.getId())) {
            throw new BusinessRuleException("Participante já inscrito neste evento");
        }

        Participacao p = new Participacao();
        p.setEvento(evento);
        p.setParticipante(participante);
        return ParticipacaoResponse.from(repository.save(p));
    }

    @Transactional(readOnly = true)
    public List<ParticipacaoResponse> listarPorEvento(Long eventoId) {
        eventoService.buscarEntidade(eventoId);
        return repository.findByEventoId(eventoId).stream().map(ParticipacaoResponse::from).toList();
    }
}