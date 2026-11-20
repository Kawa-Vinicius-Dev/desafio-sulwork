package com.sulwork.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testa o fluxo completo pela API, com banco Postgres real.
 * Cada teste roda numa transação que é desfeita no final.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContribuicaoIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private Long eventoId;
    private Long itemId;
    private Long participanteId;

    @BeforeEach
    void prepararEvento() throws Exception {
        LocalDateTime data = LocalDateTime.now().plusDays(10).withNano(0);
        eventoId = criar("/api/eventos", """
                {"nome":"Café da equipe","tipo":"CAFE_DA_MANHA",
                 "dataHora":"%s","prazoAlteracoes":"%s"}
                """.formatted(data, data.minusDays(1)));

        Long categoriaId = criar("/api/categorias", """
                {"nome":"Padaria-%d"}
                """.formatted(System.nanoTime()));

        itemId = criar("/api/eventos/" + eventoId + "/itens", """
                {"categoriaId":%d,"nome":"Pão","quantidadeNecessaria":5,"obrigatorio":true}
                """.formatted(categoriaId));

        participanteId = criar("/api/participantes", """
                {"nome":"Ana","cpf":"529.982.247-25"}
                """);
    }

    @Test
    void naoPermiteContribuirComEventoEmRascunho() throws Exception {
        contribuir(2).andExpect(status().isUnprocessableContent());
    }

    @Test
    void naoPermiteContribuirSemEstarInscrito() throws Exception {
        abrirEvento();

        contribuir(2)
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message").value("Participante não está inscrito neste evento"));
    }

    @Test
    void fluxoCompletoAtualizaOQueFalta() throws Exception {
        abrirEvento();
        inscrever();

        contribuir(2).andExpect(status().isCreated());

        mvc.perform(get("/api/eventos/{id}/faltantes", eventoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].faltam").value(3));

        contribuir(4)
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message").value("Quantidade excede o necessário. Restam 3"));

        contribuir(3).andExpect(status().isCreated());

        mvc.perform(get("/api/eventos/{id}/faltantes", eventoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private void abrirEvento() throws Exception {
        mvc.perform(patch("/api/eventos/{id}/status", eventoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"ABERTO"}
                                """))
                .andExpect(status().isOk());
    }

    private void inscrever() throws Exception {
        mvc.perform(post("/api/eventos/{id}/participacoes", eventoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"participanteId":%d}
                                """.formatted(participanteId)))
                .andExpect(status().isCreated());
    }

    private ResultActions contribuir(int quantidade) throws Exception {
        return mvc.perform(post("/api/eventos/{e}/itens/{i}/contribuicoes", eventoId, itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"participanteId":%d,"quantidade":%d}
                        """.formatted(participanteId, quantidade)));
    }

    private Long criar(String url, String json) throws Exception {
        String body = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }
}