package com.sulwork.service;

import com.sulwork.domain.Contribuicao;
import com.sulwork.domain.Evento;
import com.sulwork.domain.Item;
import com.sulwork.domain.Participante;
import com.sulwork.domain.StatusEvento;
import com.sulwork.dto.AlterarContribuicaoRequest;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.exception.ResourceNotFoundException;
import com.sulwork.repository.ContribuicaoRepository;
import com.sulwork.repository.ParticipacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContribuicaoServiceTest {

    @Mock ContribuicaoRepository repository;
    @Mock ParticipacaoRepository participacaoRepository;
    @Mock EventoService eventoService;
    @Mock ItemService itemService;
    @Mock ParticipanteService participanteService;
    @InjectMocks ContribuicaoService service;

    Evento evento;
    Item item;
    Contribuicao contribuicao;

    @BeforeEach
    void montarCenario() {
        evento = new Evento();
        evento.setId(1L);
        evento.setStatus(StatusEvento.ABERTO);
        evento.setPrazoAlteracoes(LocalDateTime.now().plusDays(1));

        item = new Item();
        item.setId(10L);
        item.setEvento(evento);
        item.setQuantidadeNecessaria(5);

        contribuicao = new Contribuicao();
        contribuicao.setId(100L);
        contribuicao.setItem(item);
        contribuicao.setQuantidade(2);

        Participante participante = new Participante();
        participante.setId(7L);
        contribuicao.setParticipante(participante);
    }

    @Test
    void alterarComQuantidadeDentroDoLimite() {
        when(repository.findByIdAndItemEventoId(100L, 1L)).thenReturn(Optional.of(contribuicao));
        when(repository.somarQuantidadePorItemExceto(10L, 100L)).thenReturn(2L);

        service.alterar(1L, 100L, new AlterarContribuicaoRequest(3));

        assertThat(contribuicao.getQuantidade()).isEqualTo(3);
    }

    @Test
    void alterarAcimaDoNecessarioLancaErro() {
        when(repository.findByIdAndItemEventoId(100L, 1L)).thenReturn(Optional.of(contribuicao));
        when(repository.somarQuantidadePorItemExceto(10L, 100L)).thenReturn(2L);

        assertThatThrownBy(() -> service.alterar(1L, 100L, new AlterarContribuicaoRequest(4)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Restam 3");
        assertThat(contribuicao.getQuantidade()).isEqualTo(2);
    }

    @Test
    void alterarDepoisDoPrazoLancaErro() {
        evento.setPrazoAlteracoes(LocalDateTime.now().minusMinutes(1));
        when(repository.findByIdAndItemEventoId(100L, 1L)).thenReturn(Optional.of(contribuicao));

        assertThatThrownBy(() -> service.alterar(1L, 100L, new AlterarContribuicaoRequest(1)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("prazo");
    }

    @Test
    void removerComEventoAbertoApagaAContribuicao() {
        when(repository.findByIdAndItemEventoId(100L, 1L)).thenReturn(Optional.of(contribuicao));

        service.remover(1L, 100L);

        verify(repository).delete(contribuicao);
    }

    @Test
    void removerComEventoEncerradoLancaErro() {
        evento.setStatus(StatusEvento.ENCERRADO);
        when(repository.findByIdAndItemEventoId(100L, 1L)).thenReturn(Optional.of(contribuicao));

        assertThatThrownBy(() -> service.remover(1L, 100L))
                .isInstanceOf(BusinessRuleException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void contribuicaoDeOutroEventoDa404() {
        when(repository.findByIdAndItemEventoId(100L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remover(2L, 100L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
