package com.cooperativa.votacao.service;

import com.cooperativa.votacao.exception.PautaNaoEncontradaException;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.repository.PautaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    @DisplayName("Deve cadastrar uma nova pauta com sucesso")
    void deveCadastrarPautaComSucesso() {
        Pauta pautaMock = Pauta.builder()
                .id(1L)
                .titulo("Aprovação Orçamentária")
                .descricao("Assembleia Anual")
                .criadaEm(LocalDateTime.now())
                .build();

        when(pautaRepository.save(any(Pauta.class))).thenReturn(pautaMock);

        Pauta salva = pautaService.cadastrarPauta("Aprovação Orçamentária", "Assembleia Anual");

        assertThat(salva).isNotNull();
        assertThat(salva.getId()).isEqualTo(1L);
        assertThat(salva.getTitulo()).isEqualTo("Aprovação Orçamentária");
        verify(pautaRepository, times(1)).save(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve buscar pauta por ID com sucesso")
    void deveBuscarPautaPorId() {
        Pauta pautaMock = Pauta.builder().id(1L).titulo("Pauta Teste").build();
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pautaMock));

        Pauta encontrada = pautaService.buscarPorId(1L);

        assertThat(encontrada).isNotNull();
        assertThat(encontrada.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar PautaNaoEncontradaException para ID inexistente")
    void deveLancarExcecaoQuandoPautaNaoExiste() {
        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pautaService.buscarPorId(99L))
                .isInstanceOf(PautaNaoEncontradaException.class);
    }

    @Test
    @DisplayName("Deve listar todas as pautas")
    void deveListarTodasAsPautas() {
        Pauta p1 = Pauta.builder().id(1L).titulo("P1").build();
        Pauta p2 = Pauta.builder().id(2L).titulo("P2").build();
        when(pautaRepository.findAllWithSessao()).thenReturn(List.of(p1, p2));

        List<Pauta> pautas = pautaService.listarTodas();

        assertThat(pautas).hasSize(2);
    }
}
