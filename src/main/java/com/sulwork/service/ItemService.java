package com.sulwork.service;

import com.sulwork.domain.Categoria;
import com.sulwork.domain.Evento;
import com.sulwork.domain.Item;
import com.sulwork.domain.StatusEvento;
import com.sulwork.dto.ItemRequest;
import com.sulwork.dto.ItemResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.exception.ResourceNotFoundException;
import com.sulwork.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class ItemService {

    private static final Set<StatusEvento> STATUS_EDITAVEIS =
            Set.of(StatusEvento.RASCUNHO, StatusEvento.ABERTO);

    private final ItemRepository repository;
    private final EventoService eventoService;
    private final CategoriaService categoriaService;

    public ItemService(
            ItemRepository repository,
            EventoService eventoService,
            CategoriaService categoriaService
    ){
        this.repository = repository;
        this.eventoService = eventoService;
        this.categoriaService = categoriaService;
    }

    @Transactional
    public ItemResponse adicionar(Long eventoId, ItemRequest request) {
        Evento evento = eventoService.buscarEntidade(eventoId);
        Categoria categoria = categoriaService.buscarEntidade(request.categoriaId());

        if (!STATUS_EDITAVEIS.contains(evento.getStatus())) {
            throw new BusinessRuleException("Não é possível adicionar itens a um evento " + evento.getStatus());
        }

        Item item = new Item();
        item.setEvento(evento);
        item.setCategoria(categoria);
        item.setNome(request.nome().trim());
        item.setQuantidadeNecessaria(request.quantidadeNecessaria());
        item.setObrigatorio(request.obrigatorio());
        item.setAlergenos(request.alergenos());
        return ItemResponse.from(repository.save(item));
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> listarPorEvento(Long eventoId) {
        eventoService.buscarEntidade(eventoId);
        return repository.findByEventoId(eventoId).stream().map(ItemResponse::from).toList();
    }

    Item buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }
}