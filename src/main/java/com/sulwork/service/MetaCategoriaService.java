package com.sulwork.service;

import com.sulwork.domain.Categoria;
import com.sulwork.domain.Evento;
import com.sulwork.domain.MetaCategoria;
import com.sulwork.domain.StatusEvento;
import com.sulwork.dto.MetaCategoriaRequest;
import com.sulwork.dto.MetaCategoriaResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.exception.ResourceNotFoundException;
import com.sulwork.repository.MetaCategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MetaCategoriaService {

    private final MetaCategoriaRepository repository;
    private final EventoService eventoService;
    private final CategoriaService categoriaService;

    public MetaCategoriaService(MetaCategoriaRepository repository,
                                EventoService eventoService,
                                CategoriaService categoriaService) {
        this.repository = repository;
        this.eventoService = eventoService;
        this.categoriaService = categoriaService;
    }

    /**
     * Cria a meta ou atualiza a que já existe (PUT idempotente):
     * cada categoria tem no máximo uma meta por evento.
     */
    @Transactional
    public MetaCategoriaResponse definir(Long eventoId, Long categoriaId, MetaCategoriaRequest request) {
        Evento evento = eventoService.buscarEntidade(eventoId);
        validarEventoEditavel(evento);
        Categoria categoria = categoriaService.buscarEntidade(categoriaId);

        MetaCategoria meta = repository.findByEventoIdAndCategoriaId(eventoId, categoriaId)
                .orElseGet(() -> {
                    MetaCategoria nova = new MetaCategoria();
                    nova.setEvento(evento);
                    nova.setCategoria(categoria);
                    return nova;
                });
        meta.setPercentual(request.percentual());
        return MetaCategoriaResponse.from(repository.save(meta));
    }

    @Transactional(readOnly = true)
    public List<MetaCategoriaResponse> listar(Long eventoId) {
        eventoService.buscarEntidade(eventoId);
        return repository.findByEventoIdOrderByCategoriaNome(eventoId).stream()
                .map(MetaCategoriaResponse::from)
                .toList();
    }

    @Transactional
    public void remover(Long eventoId, Long categoriaId) {
        validarEventoEditavel(eventoService.buscarEntidade(eventoId));
        MetaCategoria meta = repository.findByEventoIdAndCategoriaId(eventoId, categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta da categoria", categoriaId));
        repository.delete(meta);
    }

    /** Metas só mudam enquanto o evento está sendo montado ou recebendo contribuições. */
    private void validarEventoEditavel(Evento evento) {
        if (evento.getStatus() != StatusEvento.RASCUNHO && evento.getStatus() != StatusEvento.ABERTO) {
            throw new BusinessRuleException("Metas só podem ser alteradas com o evento em RASCUNHO ou ABERTO");
        }
    }
}
