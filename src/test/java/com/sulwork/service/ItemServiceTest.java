package com.sulwork.service;

import com.sulwork.domain.Categoria;
import com.sulwork.domain.Evento;
import com.sulwork.domain.Item;
import com.sulwork.domain.StatusEvento;
import com.sulwork.dto.ItemRequest;
import com.sulwork.dto.ItemResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.repository.ItemRepository;
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
class ItemServiceTest {

    @Mock ItemRepository repository;
    @Mock EventoService eventoService;
    @Mock CategoriaService categoriaService;
    @InjectMocks ItemService service;

    Evento evento;
    Categoria bebidas;
    ItemRequest cafe = new ItemRequest(5L, "  Café  ", 2, true, "cafeína");

    @BeforeEach
    void montarCenario() {
        evento = new Evento();
        evento.setId(1L);
        evento.setStatus(StatusEvento.RASCUNHO);

        bebidas = new Categoria();
        bebidas.setId(5L);
        bebidas.setNome("Bebidas");

        when(eventoService.buscarEntidade(1L)).thenReturn(evento);
        when(categoriaService.buscarEntidade(5L)).thenReturn(bebidas);
    }

    @Test
    void adicionaItemNoRascunhoSemEspacosNoNome() {
        when(repository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemResponse resposta = service.adicionar(1L, cafe);

        assertThat(resposta.nome()).isEqualTo("Café");
        assertThat(resposta.obrigatorio()).isTrue();
        assertThat(resposta.alergenos()).isEqualTo("cafeína");
    }

    @Test
    void naoAdicionaItemComEventoEncerrado() {
        evento.setStatus(StatusEvento.ENCERRADO);

        assertThatThrownBy(() -> service.adicionar(1L, cafe))
                .isInstanceOf(BusinessRuleException.class);
        verify(repository, never()).save(any());
    }
}
