package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.model.dto.request.VotoRequest;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.model.enums.OpcaoVoto;
import com.cooperativa.votacao.service.PautaService;
import com.cooperativa.votacao.service.SessaoVotacaoService;
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VotoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PautaService pautaService;

    @Autowired
    private SessaoVotacaoService sessaoService;

    @Test
    @DisplayName("Deve registrar voto 'Sim' com sucesso (HTTP 201)")
    void deveRegistrarVotoComSucesso() throws Exception {
        Pauta pauta = pautaService.cadastrarPauta("Pauta Votação IT", "Desc");
        sessaoService.abrirSessao(pauta.getId(), 5L);

        VotoRequest request = new VotoRequest("19839091069", OpcaoVoto.SIM);

        mockMvc.perform(post("/api/v1/pautas/" + pauta.getId() + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.pautaId", is(pauta.getId().intValue())))
                .andExpect(jsonPath("$.opcao", is("Sim")))
                .andExpect(jsonPath("$.cpfMascarado", notNullValue()))
                .andExpect(jsonPath("$.mensagem", is("Voto registrado com sucesso!")));
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict ao tentar votar duas vezes na mesma pauta")
    void deveRetornarConflictAoTentarVotarDuasVezes() throws Exception {
        Pauta pauta = pautaService.cadastrarPauta("Pauta Voto Duplo IT", "Desc");
        sessaoService.abrirSessao(pauta.getId(), 5L);

        VotoRequest request1 = new VotoRequest("19839091069", OpcaoVoto.SIM);
        VotoRequest request2 = new VotoRequest("19839091069", OpcaoVoto.NAO);

        // Primeiro voto
        mockMvc.perform(post("/api/v1/pautas/" + pauta.getId() + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Segundo voto do mesmo CPF
        mockMvc.perform(post("/api/v1/pautas/" + pauta.getId() + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request para CPF com formato inválido")
    void deveRetornarBadRequestParaCpfInvalido() throws Exception {
        Pauta pauta = pautaService.cadastrarPauta("Pauta CPF Inválido", "Desc");
        sessaoService.abrirSessao(pauta.getId(), 5L);

        VotoRequest request = new VotoRequest("11111111111", OpcaoVoto.SIM);

        mockMvc.perform(post("/api/v1/pautas/" + pauta.getId() + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }
}
