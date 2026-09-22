package com.cooperativa.votacao.service;

import com.cooperativa.votacao.exception.VotoDuplicadoException;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.model.entity.Voto;
import com.cooperativa.votacao.model.enums.OpcaoVoto;
import com.cooperativa.votacao.model.valueobject.Cpf;
import com.cooperativa.votacao.repository.VotoRepository;
import com.cooperativa.votacao.service.event.VotoComputadoEvent;
import com.cooperativa.votacao.service.strategy.ValidadorVotoStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoVotacaoService sessaoService;
    private final List<ValidadorVotoStrategy> validadores;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Voto registrarVoto(Long pautaId, String cpfAssociado, OpcaoVoto opcao) {
        log.info("Recebendo solicitação de voto: pautaId={}, associado={}, opcao={}", pautaId, cpfAssociado, opcao);

        Cpf cpf = new Cpf(cpfAssociado);
        String cleanCpf = cpf.getNumero();

        SessaoVotacao sessao = sessaoService.buscarPorPautaId(pautaId);

        validadores.stream()
                .sorted(Comparator.comparingInt(ValidadorVotoStrategy::getOrdem))
                .forEach(validador -> validador.validar(sessao, cleanCpf));

        Voto voto = Voto.builder()
                .sessao(sessao)
                .cpfAssociado(cleanCpf)
                .opcao(opcao)
                .dataHora(LocalDateTime.now())
                .build();

        try {
            Voto votoSalvo = votoRepository.saveAndFlush(voto);
            log.info("Voto registrado com sucesso! ID: {}, Pauta: {}, Opcao: {}", votoSalvo.getId(), pautaId, opcao);

            eventPublisher.publishEvent(new VotoComputadoEvent(
                    this,
                    votoSalvo.getId(),
                    sessao.getId(),
                    pautaId,
                    cleanCpf,
                    opcao,
                    votoSalvo.getDataHora()
            ));

            return votoSalvo;

        } catch (DataIntegrityViolationException e) {
            log.warn("Tentativa de voto concorrente duplicado detectada para associado {} na pauta {}", cleanCpf, pautaId);
            throw new VotoDuplicadoException(cpfAssociado, pautaId);
        }
    }
}
