package com.cooperativa.votacao.service;

import com.cooperativa.votacao.exception.VotoDuplicadoException;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.model.entity.Voto;
import com.cooperativa.votacao.model.enums.OpcaoVoto;
import com.cooperativa.votacao.model.enums.StatusSessao;
import com.cooperativa.votacao.repository.VotoRepository;
import com.cooperativa.votacao.service.event.VotoComputadoEvent;
import com.cooperativa.votacao.service.strategy.ValidadorVotoStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoVotacaoService sessaoService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    private List<ValidadorVotoStrategy> validadores = new ArrayList<>();

    @InjectMocks
    private VotoService votoService;

    @Test
    @DisplayName("Deve registrar voto com sucesso")
    void deveRegistrarVotoComSucesso() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta 1").build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(10L)
                .pauta(pauta)
                .status(StatusSessao.ABERTA)
                .dataAbertura(LocalDateTime.now())
                .dataFechamento(LocalDateTime.now().plusMinutes(1))
                .build();

        when(sessaoService.buscarPorPautaId(1L)).thenReturn(sessao);

        when(votoRepository.saveAndFlush(any(Voto.class))).thenAnswer(invocation -> {
            Voto v = invocation.getArgument(0);
            v.setId(100L);
            return v;
        });

        Voto votoRegistrado = votoService.registrarVoto(1L, "19839091069", OpcaoVoto.SIM);

        assertThat(votoRegistrado).isNotNull();
        assertThat(votoRegistrado.getId()).isEqualTo(100L);
        assertThat(votoRegistrado.getOpcao()).isEqualTo(OpcaoVoto.SIM);
        verify(eventPublisher, times(1)).publishEvent(any(VotoComputadoEvent.class));
    }

    @Test
    @DisplayName("Deve tratar concorrência de voto duplicado (DataIntegrityViolationException)")
    void deveTratarConcorrenciaVotoDuplicado() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta 1").build();
        SessaoVotacao sessao = SessaoVotacao.builder().id(10L).pauta(pauta).build();

        when(sessaoService.buscarPorPautaId(1L)).thenReturn(sessao);
        when(votoRepository.saveAndFlush(any(Voto.class)))
                .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

        assertThatThrownBy(() -> votoService.registrarVoto(1L, "19839091069", OpcaoVoto.NAO))
                .isInstanceOf(VotoDuplicadoException.class);
    }
}
