package com.sulwork.service;

import com.sulwork.domain.Categoria;
import com.sulwork.domain.Evento;
import com.sulwork.domain.MetaCategoria;
import com.sulwork.domain.StatusEvento;
import com.sulwork.dto.MetaCategoriaRequest;
import com.sulwork.dto.MetaCategoriaResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.repository.MetaCategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetaCategoriaServiceTest {

    @Mock MetaCategoriaRepository repository;
    @Mock EventoService eventoService;
    @Mock CategoriaService categoriaService;
    @InjectMocks MetaCategoriaService service;

    Evento evento;
    Categoria bebidas;

    @BeforeEach
    void montarCenario() {
        evento = new Evento();
        evento.setId(1L);
        evento.setStatus(StatusEvento.ABERTO);

        bebidas = new Categoria();
        bebidas.setId(5L);
        bebidas.setNome("Bebidas");
    }

    @Test
    void criaMetaQuandoCategoriaAindaNaoTem() {
        when(eventoService.buscarEntidade(1L)).thenReturn(evento);
        when(categoriaService.buscarEntidade(5L)).thenReturn(bebidas);
        when(repository.findByEventoIdAndCategoriaId(1L, 5L)).thenReturn(Optional.empty());
        when(repository.save(any(MetaCategoria.class))).thenAnswer(inv -> inv.getArgument(0));

        MetaCategoriaResponse resposta = service.definir(1L, 5L, new MetaCategoriaRequest(80));

        assertThat(resposta.percentual()).isEqualTo(80);
        assertThat(resposta.categoriaNome()).isEqualTo("Bebidas");
    }

    @Test
    void atualizaMetaExistenteEmVezDeCriarOutra() {
        MetaCategoria existente = new MetaCategoria();
        existente.setId(9L);
        existente.setEvento(evento);
        existente.setCategoria(bebidas);
        existente.setPercentual(50);
        when(eventoService.buscarEntidade(1L)).thenReturn(evento);
        when(categoriaService.buscarEntidade(5L)).thenReturn(bebidas);
        when(repository.findByEventoIdAndCategoriaId(1L, 5L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        MetaCategoriaResponse resposta = service.definir(1L, 5L, new MetaCategoriaRequest(100));

        assertThat(resposta.id()).isEqualTo(9L);
        assertThat(existente.getPercentual()).isEqualTo(100);
    }

    @Test
    void naoDefineMetaComEventoEncerrado() {
        evento.setStatus(StatusEvento.ENCERRADO);
        when(eventoService.buscarEntidade(1L)).thenReturn(evento);

        assertThatThrownBy(() -> service.definir(1L, 5L, new MetaCategoriaRequest(80)))
                .isInstanceOf(BusinessRuleException.class);
        verify(repository, never()).save(any());
    }
}
