package com.cooperativa.votacao.service;

import com.cooperativa.votacao.model.dto.response.ResultadoResponse;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.model.enums.OpcaoVoto;
import com.cooperativa.votacao.model.enums.ResultadoVotacao;
import com.cooperativa.votacao.model.enums.StatusSessao;
import com.cooperativa.votacao.repository.VotoRepository;
import com.cooperativa.votacao.repository.dto.VotoContagemProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock
    private SessaoVotacaoService sessaoService;

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private ResultadoService resultadoService;

    private SessaoVotacao criarSessaoMock() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta Teste").build();
        return SessaoVotacao.builder()
                .id(10L)
                .pauta(pauta)
                .status(StatusSessao.ENCERRADA)
                .dataAbertura(LocalDateTime.now().minusMinutes(5))
                .dataFechamento(LocalDateTime.now().minusMinutes(4))
                .build();
    }

    private VotoContagemProjection mockProjection(OpcaoVoto opcao, Long total) {
        return new VotoContagemProjection() {
            @Override
            public OpcaoVoto getOpcao() {
                return opcao;
            }

            @Override
            public Long getTotal() {
                return total;
            }
        };
    }

    @Test
    @DisplayName("Deve apurar resultado APROVADA quando votos SIM superam NAO")
    void deveApurarResultadoAprovada() {
        SessaoVotacao sessao = criarSessaoMock();
        when(sessaoService.buscarPorPautaId(1L)).thenReturn(sessao);

        when(votoRepository.obterContagemAgregadaPorSessao(10L)).thenReturn(List.of(
                mockProjection(OpcaoVoto.SIM, 120L),
                mockProjection(OpcaoVoto.NAO, 30L)
        ));

        ResultadoResponse resultado = resultadoService.apurarResultado(1L);

        assertThat(resultado.resultado()).isEqualTo(ResultadoVotacao.APROVADA);
        assertThat(resultado.totalVotos()).isEqualTo(150L);
        assertThat(resultado.votosSim()).isEqualTo(120L);
        assertThat(resultado.votosNao()).isEqualTo(30L);
    }

    @Test
    @DisplayName("Deve apurar resultado REJEITADA quando votos NAO superam SIM")
    void deveApurarResultadoRejeitada() {
        SessaoVotacao sessao = criarSessaoMock();
        when(sessaoService.buscarPorPautaId(1L)).thenReturn(sessao);

        when(votoRepository.obterContagemAgregadaPorSessao(10L)).thenReturn(List.of(
                mockProjection(OpcaoVoto.SIM, 20L),
                mockProjection(OpcaoVoto.NAO, 50L)
        ));

        ResultadoResponse resultado = resultadoService.apurarResultado(1L);

        assertThat(resultado.resultado()).isEqualTo(ResultadoVotacao.REJEITADA);
        assertThat(resultado.totalVotos()).isEqualTo(70L);
    }

    @Test
    @DisplayName("Deve apurar resultado EMPATE quando votos SIM e NAO são iguais")
    void deveApurarResultadoEmpate() {
        SessaoVotacao sessao = criarSessaoMock();
        when(sessaoService.buscarPorPautaId(1L)).thenReturn(sessao);

        when(votoRepository.obterContagemAgregadaPorSessao(10L)).thenReturn(List.of(
                mockProjection(OpcaoVoto.SIM, 50L),
                mockProjection(OpcaoVoto.NAO, 50L)
        ));

        ResultadoResponse resultado = resultadoService.apurarResultado(1L);

        assertThat(resultado.resultado()).isEqualTo(ResultadoVotacao.EMPATE);
        assertThat(resultado.totalVotos()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Deve apurar resultado SEM_VOTOS quando não houver votos registrados")
    void deveApurarResultadoSemVotos() {
        SessaoVotacao sessao = criarSessaoMock();
        when(sessaoService.buscarPorPautaId(1L)).thenReturn(sessao);
        when(votoRepository.obterContagemAgregadaPorSessao(10L)).thenReturn(List.of());

        ResultadoResponse resultado = resultadoService.apurarResultado(1L);

        assertThat(resultado.resultado()).isEqualTo(ResultadoVotacao.SEM_VOTOS);
        assertThat(resultado.totalVotos()).isEqualTo(0L);
    }
}
