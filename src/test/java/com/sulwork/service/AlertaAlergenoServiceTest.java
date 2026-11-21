package com.sulwork.service;

import com.sulwork.domain.Evento;
import com.sulwork.domain.Item;
import com.sulwork.domain.Participacao;
import com.sulwork.domain.Participante;
import com.sulwork.dto.AlertaAlergenoResponse;
import com.sulwork.repository.ItemRepository;
import com.sulwork.repository.ParticipacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertaAlergenoServiceTest {

    @Mock ItemRepository itemRepository;
    @Mock ParticipacaoRepository participacaoRepository;
    @Mock EventoService eventoService;
    @InjectMocks AlertaAlergenoService service;

    @Test
    void ignoraMaiusculaEAcento() {
        assertThat(AlertaAlergenoService.conflitos("Glúten, lactose", "gluten")).containsExactly("Glúten");
    }

    @Test
    void encontraAlergenoDentroDeUmaFraseDaRestricao() {
        assertThat(AlertaAlergenoService.conflitos("lactose", "intolerância a lactose; vegetariana"))
                .containsExactly("lactose");
    }

    @Test
    void semRestricaoOuSemAlergenoNaoGeraConflito() {
        assertThat(AlertaAlergenoService.conflitos("amendoim", null)).isEmpty();
        assertThat(AlertaAlergenoService.conflitos(null, "amendoim")).isEmpty();
        assertThat(AlertaAlergenoService.conflitos("amendoim", "lactose")).isEmpty();
    }

    @Test
    void alertaSoParaQuemEstaInscritoEComConflito() {
        Item bolo = new Item();
        bolo.setId(3L);
        bolo.setNome("Bolo");
        bolo.setAlergenos("glúten, ovo");

        Participante ana = participante(1L, "Ana", "doença celíaca (glúten)");
        Participante bruno = participante(2L, "Bruno", null);

        when(eventoService.buscarEntidade(1L)).thenReturn(new Evento());
        when(itemRepository.findByEventoId(1L)).thenReturn(List.of(bolo));
        when(participacaoRepository.findByEventoId(1L)).thenReturn(List.of(inscricao(ana), inscricao(bruno)));

        List<AlertaAlergenoResponse> alertas = service.listar(1L);

        assertThat(alertas).hasSize(1);
        assertThat(alertas.get(0).participanteNome()).isEqualTo("Ana");
        assertThat(alertas.get(0).alergenos()).containsExactly("glúten");
    }

    private Participante participante(Long id, String nome, String restricoes) {
        Participante p = new Participante();
        p.setId(id);
        p.setNome(nome);
        p.setRestricoesAlimentares(restricoes);
        return p;
    }

    private Participacao inscricao(Participante participante) {
        Participacao inscricao = new Participacao();
        inscricao.setParticipante(participante);
        return inscricao;
    }
}
