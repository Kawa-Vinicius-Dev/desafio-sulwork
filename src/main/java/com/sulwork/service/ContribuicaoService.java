package com.sulwork.service;

import com.sulwork.domain.Contribuicao;
import com.sulwork.domain.Evento;
import com.sulwork.domain.Item;
import com.sulwork.domain.Participante;
import com.sulwork.domain.StatusEvento;
import com.sulwork.dto.ContribuicaoRequest;
import com.sulwork.dto.ContribuicaoResponse;
import com.sulwork.dto.ItemFaltanteResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.exception.ResourceNotFoundException;
import com.sulwork.repository.ContribuicaoRepository;
import com.sulwork.repository.ParticipacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContribuicaoService {

    private final ContribuicaoRepository repository;
    private final ParticipacaoRepository participacaoRepository;
    private final EventoService eventoService;
    private final ItemService itemService;
    private final ParticipanteService participanteService;

    public ContribuicaoService(
            ContribuicaoRepository repository,
            ParticipacaoRepository participacaoRepository,
            EventoService eventoService,
            ItemService itemService,
            ParticipanteService participanteService
    ) {
        this.repository = repository;
        this.participacaoRepository = participacaoRepository;
        this.eventoService = eventoService;
        this.itemService = itemService;
        this.participanteService = participanteService;
    }

    @Transactional
    public ContribuicaoResponse contribuir(Long eventoId, Long itemId, ContribuicaoRequest request) {
        Evento evento = eventoService.buscarEntidade(eventoId);
        Item item = itemService.buscarEntidade(itemId);
        Participante participante = participanteService.buscarEntidade(request.participanteId());

        if (!item.getEvento().getId().equals(eventoId)) {
            throw new ResourceNotFoundException("Item", itemId);
        }
        if (evento.getStatus() != StatusEvento.ABERTO) {
            throw new BusinessRuleException("Contribuições só são permitidas com o evento ABERTO");
        }
        if (LocalDateTime.now().isAfter(evento.getPrazoAlteracoes())) {
            throw new BusinessRuleException("O prazo para contribuições deste evento já encerrou");
        }
        if (!participacaoRepository.existsByEventoIdAndParticipanteId(eventoId, participante.getId())) {
            throw new BusinessRuleException("Participante não está inscrito neste evento");
        }

        long jaConfirmado = repository.somarQuantidadePorItem(itemId);
        long restante = item.getQuantidadeNecessaria() - jaConfirmado;
        if (request.quantidade() > restante) {
            throw new BusinessRuleException("Quantidade excede o necessário. Restam " + restante);
        }

        Contribuicao c = new Contribuicao();
        c.setItem(item);
        c.setParticipante(participante);
        c.setQuantidade(request.quantidade());
        return ContribuicaoResponse.from(repository.save(c));
    }

    @Transactional(readOnly = true)
    public List<ContribuicaoResponse> listarPorEvento(Long eventoId) {
        eventoService.buscarEntidade(eventoId);
        return repository.findByItemEventoId(eventoId).stream().map(ContribuicaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemFaltanteResponse> faltantes(Long eventoId) {
        eventoService.buscarEntidade(eventoId);
        return repository.buscarFaltantes(eventoId).stream().map(ItemFaltanteResponse::from).toList();
    }
}