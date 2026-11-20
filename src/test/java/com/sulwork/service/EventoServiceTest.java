package com.sulwork.service;

import com.sulwork.domain.Evento;
import com.sulwork.domain.StatusEvento;
import com.sulwork.domain.TipoEvento;
import com.sulwork.dto.EventoRequest;
import com.sulwork.dto.EventoResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.exception.ResourceNotFoundException;
import com.sulwork.repository.EventoRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository repository;

    @InjectMocks
    private EventoService service;

    private final LocalDateTime data = LocalDateTime.now().plusDays(10);

    @Test
    void deveCriarEventoComoRascunho() {
        var req = new EventoRequest("Almoço", null, TipoEvento.ALMOCO, data, data.minusDays(2));
        when(repository.save(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        EventoResponse resp = service.criar(req);

        assertThat(resp.nome()).isEqualTo("Almoço");
        assertThat(resp.status()).isEqualTo(StatusEvento.RASCUNHO);
        verify(repository).save(any(Evento.class));
    }

    @Test
    void naoDeveCriarQuandoPrazoDepoisDaData() {
        var req = new EventoRequest("Almoço", null, TipoEvento.ALMOCO, data, data.plusDays(1));

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(BusinessRuleException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancar404QuandoEventoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}