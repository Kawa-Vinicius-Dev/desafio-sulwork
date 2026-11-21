package com.sulwork.service;

import com.sulwork.domain.MetaCategoria;
import com.sulwork.dto.CoberturaCategoriaResponse;
import com.sulwork.dto.CoberturaResponse;
import com.sulwork.repository.ContribuicaoRepository;
import com.sulwork.repository.ItemCoberturaView;
import com.sulwork.repository.MetaCategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Calcula quanto do evento já está garantido.
 * O SQL traz os números de cada item; as regras ficam aqui, em Java, para poderem ser testadas sem banco.
 */
@Service
public class CoberturaService {

    /** Item obrigatório conta em dobro no percentual geral. */
    private static final int PESO_OBRIGATORIO = 2;

    private final ContribuicaoRepository contribuicaoRepository;
    private final MetaCategoriaRepository metaRepository;
    private final EventoService eventoService;

    public CoberturaService(ContribuicaoRepository contribuicaoRepository,
                            MetaCategoriaRepository metaRepository,
                            EventoService eventoService) {
        this.contribuicaoRepository = contribuicaoRepository;
        this.metaRepository = metaRepository;
        this.eventoService = eventoService;
    }

    @Transactional(readOnly = true)
    public CoberturaResponse calcular(Long eventoId) {
        eventoService.buscarEntidade(eventoId);
        List<ItemCoberturaView> itens = contribuicaoRepository.buscarCobertura(eventoId);
        Map<Long, Integer> metas = metaRepository.findByEventoIdOrderByCategoriaNome(eventoId).stream()
                .collect(Collectors.toMap(m -> m.getCategoria().getId(), MetaCategoria::getPercentual));

        List<CoberturaCategoriaResponse> categorias = porCategoria(itens, metas);
        boolean obrigatoriosCompletos = itens.stream()
                .filter(ItemCoberturaView::getObrigatorio)
                .allMatch(i -> coberto(i) == i.getQuantidadeNecessaria());
        boolean metasAtingidas = categorias.stream().allMatch(CoberturaCategoriaResponse::metaAtingida);
        boolean pronto = !itens.isEmpty() && obrigatoriosCompletos && metasAtingidas;

        return new CoberturaResponse(percentualGeral(itens), obrigatoriosCompletos, pronto, categorias);
    }

    private List<CoberturaCategoriaResponse> porCategoria(List<ItemCoberturaView> itens, Map<Long, Integer> metas) {
        // LinkedHashMap mantém a ordem do SQL (por nome da categoria)
        Map<Long, List<ItemCoberturaView>> grupos = itens.stream()
                .collect(Collectors.groupingBy(ItemCoberturaView::getCategoriaId, LinkedHashMap::new, Collectors.toList()));

        List<CoberturaCategoriaResponse> resultado = new ArrayList<>();
        grupos.forEach((categoriaId, doGrupo) -> {
            long coberto = doGrupo.stream().mapToLong(this::coberto).sum();
            long necessario = doGrupo.stream().mapToLong(ItemCoberturaView::getQuantidadeNecessaria).sum();
            int percentual = percentual(coberto, necessario);
            Integer meta = metas.get(categoriaId);
            boolean atingida = meta == null || percentual >= meta;
            resultado.add(new CoberturaCategoriaResponse(
                    categoriaId, doGrupo.get(0).getCategoriaNome(), percentual, meta, atingida));
        });
        return resultado;
    }

    private int percentualGeral(List<ItemCoberturaView> itens) {
        long coberto = somaPonderada(itens, this::coberto);
        long necessario = somaPonderada(itens, i -> (long) i.getQuantidadeNecessaria());
        return percentual(coberto, necessario);
    }

    private long somaPonderada(List<ItemCoberturaView> itens, Function<ItemCoberturaView, Long> valor) {
        return itens.stream()
                .mapToLong(i -> valor.apply(i) * (i.getObrigatorio() ? PESO_OBRIGATORIO : 1))
                .sum();
    }

    /** O que passa do necessário não conta: 30 pães para 20 pedidos cobrem 20. */
    private long coberto(ItemCoberturaView item) {
        return Math.min(item.getQuantidadeConfirmada(), item.getQuantidadeNecessaria());
    }

    /** Arredonda para baixo: 99,9% não pode aparecer como 100%. */
    private int percentual(long coberto, long necessario) {
        return necessario == 0 ? 0 : (int) (coberto * 100 / necessario);
    }
}
