package com.sulwork.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Confere pela API o formato das respostas de erro e as regras de status.
 * Cada teste é desfeito no final (@Transactional).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventoApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void criaEventoComoRascunhoEDevolveOLocation() throws Exception {
        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(eventoValido()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("RASCUNHO"));
    }

    @Test
    void campoObrigatorioFaltandoDevolve400ComOsCampos() throws Exception {
        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados inválidos"))
                .andExpect(jsonPath("$.fields.nome").exists())
                .andExpect(jsonPath("$.fields.tipo").exists());
    }

    @Test
    void naoPulaDeRascunhoParaRealizado() throws Exception {
        Long id = criarEvento();

        mvc.perform(patch("/api/eventos/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"REALIZADO\"}"))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void eventoInexistenteDevolve404() throws Exception {
        mvc.perform(get("/api/eventos/{id}", 999_999))
                .andExpect(status().isNotFound());
    }

    @Test
    void cpfInvalidoDevolve400() throws Exception {
        mvc.perform(post("/api/participantes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ana\",\"cpf\":\"111.111.111-11\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.cpf").value("CPF inválido"));
    }

    @Test
    void coberturaDeEventoSemItensEZero() throws Exception {
        Long id = criarEvento();

        mvc.perform(get("/api/eventos/{id}/cobertura", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percentualGeral").value(0))
                .andExpect(jsonPath("$.pronto").value(false));
    }

    private String eventoValido() {
        LocalDateTime data = LocalDateTime.now().plusDays(10).withNano(0);
        return """
                {"nome":"Almoço do time","tipo":"ALMOCO","dataHora":"%s","prazoAlteracoes":"%s"}
                """.formatted(data, data.minusDays(1));
    }

    private Long criarEvento() throws Exception {
        String body = mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(eventoValido()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }
}
