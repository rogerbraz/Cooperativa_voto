package com.cooperativa.votacao.service.strategy;

import com.cooperativa.votacao.exception.AssociadoNaoHabilitadoException;
import com.cooperativa.votacao.exception.SessaoEncerradaException;
import com.cooperativa.votacao.exception.VotoDuplicadoException;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.model.enums.StatusAssociadoVoto;
import com.cooperativa.votacao.model.enums.StatusSessao;
import com.cooperativa.votacao.repository.VotoRepository;
import com.cooperativa.votacao.service.client.UserInfoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadorVotoStrategyTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private UserInfoClient userInfoClient;

    @Test
    @DisplayName("ValidadorSessaoAbertaStrategy: Deve validar com sucesso sessão aberta")
    void deveValidarSessaoAbertaComSucesso() {
        ValidadorSessaoAbertaStrategy strategy = new ValidadorSessaoAbertaStrategy();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(1L)
                .status(StatusSessao.ABERTA)
                .dataAbertura(LocalDateTime.now().minusSeconds(10))
                .dataFechamento(LocalDateTime.now().plusMinutes(5))
                .build();

        assertThatCode(() -> strategy.validar(sessao, "19839091069"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ValidadorSessaoAbertaStrategy: Deve lançar exceção para sessão com data expirada")
    void deveLancarExcecaoParaSessaoExpirada() {
        ValidadorSessaoAbertaStrategy strategy = new ValidadorSessaoAbertaStrategy();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(1L)
                .status(StatusSessao.ABERTA)
                .dataAbertura(LocalDateTime.now().minusMinutes(10))
                .dataFechamento(LocalDateTime.now().minusMinutes(5))
                .build();

        assertThatThrownBy(() -> strategy.validar(sessao, "19839091069"))
                .isInstanceOf(SessaoEncerradaException.class);
    }

    @Test
    @DisplayName("ValidadorVotoUnicoStrategy: Deve lançar exceção se associado já votou na pauta")
    void deveLancarExcecaoParaVotoDuplicado() {
        ValidadorVotoUnicoStrategy strategy = new ValidadorVotoUnicoStrategy(votoRepository);
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta Teste").build();
        SessaoVotacao sessao = SessaoVotacao.builder().id(10L).pauta(pauta).build();

        when(votoRepository.existsBySessaoIdAndCpfAssociado(10L, "19839091069")).thenReturn(true);

        assertThatThrownBy(() -> strategy.validar(sessao, "19839091069"))
                .isInstanceOf(VotoDuplicadoException.class);
    }

    @Test
    @DisplayName("ValidadorCpfHabilitadoStrategy: Deve lançar exceção se status for UNABLE_TO_VOTE")
    void deveLancarExcecaoQuandoAssociadoNaoEstiverHabilitado() {
        ValidadorCpfHabilitadoStrategy strategy = new ValidadorCpfHabilitadoStrategy(userInfoClient);
        SessaoVotacao sessao = SessaoVotacao.builder().id(10L).build();

        when(userInfoClient.verificarStatusVoto("19839091069")).thenReturn(StatusAssociadoVoto.UNABLE_TO_VOTE);

        assertThatThrownBy(() -> strategy.validar(sessao, "19839091069"))
                .isInstanceOf(AssociadoNaoHabilitadoException.class);
    }

    @Test
    @DisplayName("ValidadorCpfHabilitadoStrategy: Deve permitir voto se status for ABLE_TO_VOTE")
    void devePermitirVotoQuandoHabilitado() {
        ValidadorCpfHabilitadoStrategy strategy = new ValidadorCpfHabilitadoStrategy(userInfoClient);
        SessaoVotacao sessao = SessaoVotacao.builder().id(10L).build();

        when(userInfoClient.verificarStatusVoto("19839091069")).thenReturn(StatusAssociadoVoto.ABLE_TO_VOTE);

        assertThatCode(() -> strategy.validar(sessao, "19839091069"))
                .doesNotThrowAnyException();
    }
}
