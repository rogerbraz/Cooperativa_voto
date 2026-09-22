package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.model.dto.request.NovaPautaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PautaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve cadastrar pauta com sucesso (HTTP 201)")
    void deveCadastrarPautaComSucesso() throws Exception {
        NovaPautaRequest request = new NovaPautaRequest("Aprovação Contas 2026", "Apreciação de relatório contábil anual");

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.titulo", is("Aprovação Contas 2026")))
                .andExpect(jsonPath("$.descricao", is("Apreciação de relatório contábil anual")))
                .andExpect(jsonPath("$.criadaEm", notNullValue()))
                .andExpect(jsonPath("$.sessaoAberta", is(false)));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar cadastrar pauta sem título")
    void deveRetornarBadRequestAoCadastrarPautaSemTitulo() throws Exception {
        NovaPautaRequest request = new NovaPautaRequest("", "Descrição sem título");

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.fieldErrors.titulo", notNullValue()));
    }

    @Test
    @DisplayName("Deve listar pautas cadastradas (HTTP 200)")
    void deveListarPautas() throws Exception {
        NovaPautaRequest p1 = new NovaPautaRequest("Pauta Listagem 1", "Desc 1");
        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p1)));

        mockMvc.perform(get("/api/v1/pautas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }
}
