package com.sulwork.service;

import com.sulwork.domain.Categoria;
import com.sulwork.domain.Evento;
import com.sulwork.domain.MetaCategoria;
import com.sulwork.dto.CoberturaCategoriaResponse;
import com.sulwork.dto.CoberturaResponse;
import com.sulwork.repository.ContribuicaoRepository;
import com.sulwork.repository.ItemCoberturaView;
import com.sulwork.repository.MetaCategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoberturaServiceTest {

    @Mock ContribuicaoRepository contribuicaoRepository;
    @Mock MetaCategoriaRepository metaRepository;
    @Mock EventoService eventoService;
    @InjectMocks CoberturaService service;

    /** Linha falsa do SQL, para montar o cenário sem banco. */
    record Linha(Long itemId, Long categoriaId, String categoriaNome, Integer quantidadeNecessaria,
                 Long quantidadeConfirmada, Boolean obrigatorio) implements ItemCoberturaView {
        public Long getItemId() { return itemId; }
        public Long getCategoriaId() { return categoriaId; }
        public String getCategoriaNome() { return categoriaNome; }
        public Integer getQuantidadeNecessaria() { return quantidadeNecessaria; }
        public Long getQuantidadeConfirmada() { return quantidadeConfirmada; }
        public Boolean getObrigatorio() { return obrigatorio; }
    }

    private void cenario(List<ItemCoberturaView> itens, List<MetaCategoria> metas) {
        when(eventoService.buscarEntidade(1L)).thenReturn(new Evento());
        when(contribuicaoRepository.buscarCobertura(1L)).thenReturn(itens);
        when(metaRepository.findByEventoIdOrderByCategoriaNome(1L)).thenReturn(metas);
    }

    private MetaCategoria meta(Long categoriaId, int percentual) {
        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        MetaCategoria meta = new MetaCategoria();
        meta.setCategoria(categoria);
        meta.setPercentual(percentual);
        return meta;
    }

    @Test
    void contribuicaoAcimaDoNecessarioNaoPassaDeCemPorCento() {
        cenario(List.of(new Linha(1L, 10L, "Padaria", 20, 30L, false)), List.of());

        CoberturaResponse cobertura = service.calcular(1L);

        assertThat(cobertura.percentualGeral()).isEqualTo(100);
        assertThat(cobertura.pronto()).isTrue();
    }

    @Test
    void obrigatorioContaEmDobroNoPercentualGeral() {
        // obrigatório: 0 de 10 (peso 2 -> 0 de 20). opcional: 10 de 10 (peso 1 -> 10 de 10). 10/30 = 33%
        cenario(List.of(
                new Linha(1L, 10L, "Bebidas", 10, 0L, true),
                new Linha(2L, 20L, "Doces", 10, 10L, false)), List.of());

        CoberturaResponse cobertura = service.calcular(1L);

        assertThat(cobertura.percentualGeral()).isEqualTo(33);
        assertThat(cobertura.obrigatoriosCompletos()).isFalse();
        assertThat(cobertura.pronto()).isFalse();
    }

    @Test
    void naoFicaProntoSeUmaCategoriaNaoBateuAMeta() {
        cenario(List.of(new Linha(1L, 20L, "Doces", 10, 4L, false)), List.of(meta(20L, 50)));

        CoberturaResponse cobertura = service.calcular(1L);

        CoberturaCategoriaResponse doces = cobertura.categorias().get(0);
        assertThat(doces.percentual()).isEqualTo(40);
        assertThat(doces.meta()).isEqualTo(50);
        assertThat(doces.metaAtingida()).isFalse();
        assertThat(cobertura.pronto()).isFalse();
    }

    @Test
    void eventoSemItensNaoEstaPronto() {
        cenario(List.of(), List.of());

        CoberturaResponse cobertura = service.calcular(1L);

        assertThat(cobertura.percentualGeral()).isZero();
        assertThat(cobertura.pronto()).isFalse();
    }
}
