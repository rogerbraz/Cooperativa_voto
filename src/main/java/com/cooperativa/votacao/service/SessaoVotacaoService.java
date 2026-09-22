package com.cooperativa.votacao.service;

import com.cooperativa.votacao.exception.SessaoJaExisteException;
import com.cooperativa.votacao.exception.SessaoNaoEncontradaException;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.model.enums.StatusSessao;
import com.cooperativa.votacao.repository.SessaoVotacaoRepository;
import com.cooperativa.votacao.service.event.SessaoAbertaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessaoVotacaoService {

    public static final long DEFAULT_DURACAO_MINUTOS = 1L;

    private final SessaoVotacaoRepository sessaoRepository;
    private final PautaService pautaService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SessaoVotacao abrirSessao(Long pautaId, Long duracaoMinutos) {
        log.info("Tentativa de abertura de sessão para pauta ID: {}, duração: {} min", pautaId, duracaoMinutos);

        Pauta pauta = pautaService.buscarPorId(pautaId);

        if (sessaoRepository.existsByPautaId(pautaId)) {
            throw new SessaoJaExisteException(pautaId);
        }

        long duracao = (duracaoMinutos != null && duracaoMinutos > 0) ? duracaoMinutos : DEFAULT_DURACAO_MINUTOS;

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime fechamento = agora.plusMinutes(duracao);

        SessaoVotacao sessao = SessaoVotacao.builder()
                .pauta(pauta)
                .dataAbertura(agora)
                .dataFechamento(fechamento)
                .status(StatusSessao.ABERTA)
                .build();

        SessaoVotacao salva = sessaoRepository.save(sessao);
        log.info("Sessão ID {} aberta com sucesso para pauta {}. Fechamento previsto: {}", salva.getId(), pauta.getId(), fechamento);

        eventPublisher.publishEvent(new SessaoAbertaEvent(this, salva.getId(), pauta.getId(), pauta.getTitulo(), fechamento));

        return salva;
    }

    @Transactional
    public SessaoVotacao buscarPorPautaId(Long pautaId) {
        SessaoVotacao sessao = sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> new SessaoNaoEncontradaException(pautaId));

        if (sessao.isAberta() && sessao.isExpirada(LocalDateTime.now())) {
            sessao.encerrar();
            sessaoRepository.save(sessao);
        }

        return sessao;
    }

    @Transactional
    public SessaoVotacao buscarPorId(Long sessaoId) {
        SessaoVotacao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new SessaoNaoEncontradaException(sessaoId));

        if (sessao.isAberta() && sessao.isExpirada(LocalDateTime.now())) {
            sessao.encerrar();
            sessaoRepository.save(sessao);
        }

        return sessao;
    }

    @Transactional(readOnly = true)
    public List<SessaoVotacao> listarSessoesAbertas() {
        return sessaoRepository.findSessoesAbertas(LocalDateTime.now());
    }
}
