package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.model.enums.OpcaoVoto;
import com.cooperativa.votacao.service.PautaService;
import com.cooperativa.votacao.service.SessaoVotacaoService;
import com.cooperativa.votacao.service.VotoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ResultadoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaService pautaService;

    @Autowired
    private SessaoVotacaoService sessaoService;

    @Autowired
    private VotoService votoService;

    @Test
    @DisplayName("Deve apurar e retornar o resultado da votação corretamente")
    void deveApurarResultadoCorretamente() throws Exception {
        Pauta pauta = pautaService.cadastrarPauta("Pauta Resultado IT", "Desc");
        sessaoService.abrirSessao(pauta.getId(), 5L);

        votoService.registrarVoto(pauta.getId(), "19839091069", OpcaoVoto.SIM);
        votoService.registrarVoto(pauta.getId(), "62289608068", OpcaoVoto.SIM);

        mockMvc.perform(get("/api/v1/pautas/" + pauta.getId() + "/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId", is(pauta.getId().intValue())))
                .andExpect(jsonPath("$.totalVotos", is(2)))
                .andExpect(jsonPath("$.votosSim", is(2)))
                .andExpect(jsonPath("$.votosNao", is(0)))
                .andExpect(jsonPath("$.resultado", is("APROVADA")));
    }
}
