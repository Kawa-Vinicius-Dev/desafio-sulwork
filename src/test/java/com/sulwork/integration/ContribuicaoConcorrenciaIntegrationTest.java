package com.sulwork.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Duas pessoas contribuindo no mesmo item ao mesmo tempo.
 * Sem @Transactional de propósito: cada requisição precisa da sua própria transação
 * para o lock do item fazer efeito. Por isso o @AfterEach apaga o que o teste gravou.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ContribuicaoConcorrenciaIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JdbcTemplate jdbc;

    private Long eventoId;
    private Long categoriaId;
    private final List<Long> participantes = new ArrayList<>();

    @AfterEach
    void limparBanco() {
        jdbc.update("DELETE FROM contribuicao WHERE item_id IN (SELECT id FROM item WHERE evento_id = ?)", eventoId);
        jdbc.update("DELETE FROM participacao WHERE evento_id = ?", eventoId);
        jdbc.update("DELETE FROM item WHERE evento_id = ?", eventoId);
        jdbc.update("DELETE FROM evento WHERE id = ?", eventoId);
        jdbc.update("DELETE FROM categoria WHERE id = ?", categoriaId);
        participantes.forEach(id -> jdbc.update("DELETE FROM participante WHERE id = ?", id));
    }

    @Test
    void contribuicoesSimultaneasNaoPassamDaQuantidadeNecessaria() throws Exception {
        LocalDateTime data = LocalDateTime.now().plusDays(10).withNano(0);
        eventoId = criar("/api/eventos", """
                {"nome":"Concorrência","tipo":"LANCHE","dataHora":"%s","prazoAlteracoes":"%s"}
                """.formatted(data, data.minusDays(1)));
        categoriaId = criar("/api/categorias", """
                {"nome":"Conc-%d"}
                """.formatted(System.nanoTime()));
        Long itemId = criar("/api/eventos/" + eventoId + "/itens", """
                {"categoriaId":%d,"nome":"Suco","quantidadeNecessaria":5,"obrigatorio":true}
                """.formatted(categoriaId));
        mvc.perform(patch("/api/eventos/{id}/status", eventoId)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"ABERTO\"}"))
                .andExpect(status().isOk());

        participantes.add(participante());
        participantes.add(participante());
        for (Long p : participantes) {
            mvc.perform(post("/api/eventos/{id}/participacoes", eventoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"participanteId\":%d}".formatted(p)))
                    .andExpect(status().isCreated());
        }

        // As duas pedem 3 de 5 ao mesmo tempo: só uma pode entrar.
        CountDownLatch largada = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        List<Future<Integer>> respostas = new ArrayList<>();
        for (Long p : participantes) {
            respostas.add(pool.submit(() -> {
                largada.await();
                return mvc.perform(post("/api/eventos/{e}/itens/{i}/contribuicoes", eventoId, itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"participanteId\":%d,\"quantidade\":3}".formatted(p)))
                        .andReturn().getResponse().getStatus();
            }));
        }
        largada.countDown();
        List<Integer> status = new ArrayList<>();
        for (Future<Integer> r : respostas) {
            status.add(r.get());
        }
        pool.shutdown();

        assertThat(status).containsExactlyInAnyOrder(201, 422);
        String faltantes = mvc.perform(get("/api/eventos/{id}/faltantes", eventoId))
                .andReturn().getResponse().getContentAsString();
        assertThat(((Number) JsonPath.read(faltantes, "$[0].faltam")).intValue()).isEqualTo(2);
    }

    private Long participante() throws Exception {
        return criar("/api/participantes", """
                {"nome":"Pessoa","cpf":"%s"}
                """.formatted(cpfValido()));
    }

    /** Gera um CPF válido diferente a cada chamada, porque este teste não desfaz o que grava. */
    private static String cpfValido() {
        int[] d = new int[11];
        long semente = System.nanoTime();
        for (int i = 0; i < 9; i++) {
            d[i] = (int) ((semente >> (i * 3)) % 10 + 10) % 10;
        }
        d[0] = (d[0] % 9) + 1;
        for (int pos = 9; pos < 11; pos++) {
            int soma = 0;
            for (int i = 0; i < pos; i++) {
                soma += d[i] * (pos + 1 - i);
            }
            int resto = soma % 11;
            d[pos] = resto < 2 ? 0 : 11 - resto;
        }
        StringBuilder sb = new StringBuilder();
        for (int x : d) sb.append(x);
        return sb.toString();
    }

    private Long criar(String url, String json) throws Exception {
        String body = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }
}
