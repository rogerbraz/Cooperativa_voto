package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.model.dto.request.AbrirSessaoRequest;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.service.PautaService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SessaoVotacaoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PautaService pautaService;

    @Test
    @DisplayName("Deve abrir sessão de votação com sucesso (HTTP 201)")
    void deveAbrirSessaoComSucesso() throws Exception {
        Pauta pauta = pautaService.cadastrarPauta("Pauta Sessão IT", "Desc");

        AbrirSessaoRequest request = new AbrirSessaoRequest(pauta.getId(), 5L);

        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.pautaId", is(pauta.getId().intValue())))
                .andExpect(jsonPath("$.status", is("ABERTA")))
                .andExpect(jsonPath("$.aberta", is(true)))
                .andExpect(jsonPath("$.segundosRestantes", greaterThan(0)));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar abrir sessão para pauta inexistente")
    void deveRetornarNotFoundParaPautaInexistente() throws Exception {
        AbrirSessaoRequest request = new AbrirSessaoRequest(9999L, 1L);

        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict ao tentar abrir segunda sessão para a mesma pauta")
    void deveRetornarConflictParaSessaoDuplicada() throws Exception {
        Pauta pauta = pautaService.cadastrarPauta("Pauta Duplicidade", "Desc");
        AbrirSessaoRequest request = new AbrirSessaoRequest(pauta.getId(), 1L);

        // Primeira abertura
        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Segunda abertura para mesma pauta
        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }
}
