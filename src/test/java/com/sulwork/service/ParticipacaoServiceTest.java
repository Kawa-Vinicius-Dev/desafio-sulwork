package com.sulwork.service;

import com.sulwork.domain.Evento;
import com.sulwork.domain.Participacao;
import com.sulwork.domain.Participante;
import com.sulwork.domain.StatusEvento;
import com.sulwork.dto.ParticipacaoRequest;
import com.sulwork.dto.ParticipacaoResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.repository.ParticipacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipacaoServiceTest {

    @Mock ParticipacaoRepository repository;
    @Mock EventoService eventoService;
    @Mock ParticipanteService participanteService;
    @InjectMocks ParticipacaoService service;

    Evento evento;
    Participante ana;

    @BeforeEach
    void montarCenario() {
        evento = new Evento();
        evento.setId(1L);
        evento.setStatus(StatusEvento.ABERTO);

        ana = new Participante();
        ana.setId(2L);
        ana.setNome("Ana");

        when(eventoService.buscarEntidade(1L)).thenReturn(evento);
        when(participanteService.buscarEntidade(2L)).thenReturn(ana);
    }

    @Test
    void inscreveComEventoAberto() {
        when(repository.existsByEventoIdAndParticipanteId(1L, 2L)).thenReturn(false);
        when(repository.save(any(Participacao.class))).thenAnswer(inv -> inv.getArgument(0));

        ParticipacaoResponse resposta = service.inscrever(1L, new ParticipacaoRequest(2L));

        assertThat(resposta.participanteNome()).isEqualTo("Ana");
    }

    @Test
    void naoInscreveDuasVezesNoMesmoEvento() {
        when(repository.existsByEventoIdAndParticipanteId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> service.inscrever(1L, new ParticipacaoRequest(2L)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Participante já inscrito neste evento");
        verify(repository, never()).save(any());
    }

    @Test
    void naoInscreveForaDoStatusAberto() {
        evento.setStatus(StatusEvento.RASCUNHO);

        assertThatThrownBy(() -> service.inscrever(1L, new ParticipacaoRequest(2L)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Inscrições só são permitidas com o evento ABERTO");
        verify(repository, never()).save(any());
    }
}
