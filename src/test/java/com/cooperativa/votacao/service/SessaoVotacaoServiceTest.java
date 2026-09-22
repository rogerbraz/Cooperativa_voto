package com.cooperativa.votacao.service;

import com.cooperativa.votacao.exception.SessaoJaExisteException;
import com.cooperativa.votacao.exception.SessaoNaoEncontradaException;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.model.enums.StatusSessao;
import com.cooperativa.votacao.repository.SessaoVotacaoRepository;
import com.cooperativa.votacao.service.event.SessaoAbertaEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private PautaService pautaService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SessaoVotacaoService sessaoService;

    @Test
    @DisplayName("Deve abrir sessão com duração padrão de 1 minuto quando duração não for informada")
    void deveAbrirSessaoComDuracaoPadrao() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta 1").build();
        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(false);

        when(sessaoRepository.save(any(SessaoVotacao.class))).thenAnswer(invocation -> {
            SessaoVotacao s = invocation.getArgument(0);
            s.setId(10L);
            return s;
        });

        SessaoVotacao sessao = sessaoService.abrirSessao(1L, null);

        assertThat(sessao).isNotNull();
        assertThat(sessao.getId()).isEqualTo(10L);
        assertThat(sessao.getStatus()).isEqualTo(StatusSessao.ABERTA);
        assertThat(sessao.getDataFechamento()).isAfter(sessao.getDataAbertura());
        verify(eventPublisher, times(1)).publishEvent(any(SessaoAbertaEvent.class));
    }

    @Test
    @DisplayName("Deve abrir sessão com duração personalizada")
    void deveAbrirSessaoComDuracaoPersonalizada() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta 1").build();
        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(false);

        when(sessaoRepository.save(any(SessaoVotacao.class))).thenAnswer(invocation -> {
            SessaoVotacao s = invocation.getArgument(0);
            s.setId(10L);
            return s;
        });

        SessaoVotacao sessao = sessaoService.abrirSessao(1L, 10L);

        assertThat(sessao).isNotNull();
        assertThat(sessao.getDataFechamento()).isAfterOrEqualTo(sessao.getDataAbertura().plusMinutes(9));
    }

    @Test
    @DisplayName("Deve lançar SessaoJaExisteException se pauta já possui sessão cadastrada")
    void deveLancarExcecaoQuandoSessaoJaExisteParaPauta() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta 1").build();
        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> sessaoService.abrirSessao(1L, 5L))
                .isInstanceOf(SessaoJaExisteException.class);

        verify(sessaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar SessaoNaoEncontradaException para pauta sem sessão")
    void deveLancarExcecaoQuandoSessaoNaoEncontrada() {
        when(sessaoRepository.findByPautaId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessaoService.buscarPorPautaId(99L))
                .isInstanceOf(SessaoNaoEncontradaException.class);
    }
}
