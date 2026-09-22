package com.cooperativa.votacao.service;

import com.cooperativa.votacao.model.dto.response.ResultadoResponse;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.model.enums.OpcaoVoto;
import com.cooperativa.votacao.model.enums.ResultadoVotacao;
import com.cooperativa.votacao.repository.VotoRepository;
import com.cooperativa.votacao.repository.dto.VotoContagemProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultadoService {

    private final SessaoVotacaoService sessaoService;
    private final VotoRepository votoRepository;

    @Transactional
    public ResultadoResponse apurarResultado(Long pautaId) {
        log.info("Apurando resultado para pauta ID: {}", pautaId);

        SessaoVotacao sessao = sessaoService.buscarPorPautaId(pautaId);
        Pauta pauta = sessao.getPauta();

        List<VotoContagemProjection> contagens = votoRepository.obterContagemAgregadaPorSessao(sessao.getId());

        long votosSim = 0L;
        long votosNao = 0L;

        for (VotoContagemProjection c : contagens) {
            if (c.getOpcao() == OpcaoVoto.SIM) {
                votosSim = c.getTotal() != null ? c.getTotal() : 0L;
            } else if (c.getOpcao() == OpcaoVoto.NAO) {
                votosNao = c.getTotal() != null ? c.getTotal() : 0L;
            }
        }

        long totalVotos = votosSim + votosNao;
        ResultadoVotacao resultado;

        if (totalVotos == 0) {
            resultado = ResultadoVotacao.SEM_VOTOS;
        } else if (votosSim > votosNao) {
            resultado = ResultadoVotacao.APROVADA;
        } else if (votosNao > votosSim) {
            resultado = ResultadoVotacao.REJEITADA;
        } else {
            resultado = ResultadoVotacao.EMPATE;
        }

        log.info("Resultado apurado para pauta {}: {} (Total={}, Sim={}, Não={})",
                pautaId, resultado, totalVotos, votosSim, votosNao);

        return ResultadoResponse.builder()
                .pautaId(pauta.getId())
                .pautaTitulo(pauta.getTitulo())
                .sessaoId(sessao.getId())
                .statusSessao(sessao.getStatus())
                .sessaoAberta(sessao.isAberta())
                .dataAbertura(sessao.getDataAbertura())
                .dataFechamento(sessao.getDataFechamento())
                .totalVotos(totalVotos)
                .votosSim(votosSim)
                .votosNao(votosNao)
                .resultado(resultado)
                .build();
    }
}
