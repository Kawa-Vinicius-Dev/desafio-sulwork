package com.sulwork.service;

import com.sulwork.domain.Item;
import com.sulwork.domain.Participante;
import com.sulwork.dto.AlertaAlergenoResponse;
import com.sulwork.repository.ItemRepository;
import com.sulwork.repository.ParticipacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Cruza os alérgenos de cada item com as restrições alimentares dos inscritos.
 * Os dois campos são texto livre separado por vírgula, ex.: "glúten, lactose".
 * Serve de aviso para o organizador; não bloqueia nada.
 */
@Service
public class AlertaAlergenoService {

    private final ItemRepository itemRepository;
    private final ParticipacaoRepository participacaoRepository;
    private final EventoService eventoService;

    public AlertaAlergenoService(ItemRepository itemRepository,
                                 ParticipacaoRepository participacaoRepository,
                                 EventoService eventoService) {
        this.itemRepository = itemRepository;
        this.participacaoRepository = participacaoRepository;
        this.eventoService = eventoService;
    }

    @Transactional(readOnly = true)
    public List<AlertaAlergenoResponse> listar(Long eventoId) {
        eventoService.buscarEntidade(eventoId);
        List<Item> itens = itemRepository.findByEventoId(eventoId);
        List<Participante> inscritos = participacaoRepository.findByEventoId(eventoId).stream()
                .map(p -> p.getParticipante())
                .toList();

        List<AlertaAlergenoResponse> alertas = new ArrayList<>();
        for (Item item : itens) {
            for (Participante participante : inscritos) {
                List<String> conflitos = conflitos(item.getAlergenos(), participante.getRestricoesAlimentares());
                if (!conflitos.isEmpty()) {
                    alertas.add(new AlertaAlergenoResponse(item.getId(), item.getNome(),
                            participante.getId(), participante.getNome(), conflitos));
                }
            }
        }
        return alertas;
    }

    /**
     * Alérgenos do item que aparecem nas restrições da pessoa.
     * "contém" em vez de "igual" para pegar "intolerância a lactose" quando o item diz "lactose".
     */
    static List<String> conflitos(String alergenosDoItem, String restricoes) {
        List<String> restricoesNormalizadas = termos(restricoes).stream().map(AlertaAlergenoService::normalizar).toList();
        return termos(alergenosDoItem).stream()
                .filter(alergeno -> restricoesNormalizadas.stream()
                        .anyMatch(restricao -> restricao.contains(normalizar(alergeno))))
                .toList();
    }

    private static List<String> termos(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return Arrays.stream(texto.split("[,;]"))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .toList();
    }

    /** Minúsculas e sem acento: "Glúten" e "gluten" são a mesma coisa. */
    private static String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT);
    }
}
