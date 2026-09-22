package com.cooperativa.votacao.service.strategy;

import com.cooperativa.votacao.exception.VotoDuplicadoException;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class ValidadorVotoUnicoStrategy implements ValidadorVotoStrategy {

    private final VotoRepository votoRepository;

    @Override
    public void validar(SessaoVotacao sessao, String cpfAssociado) {
        String cleanCpf = cpfAssociado.replaceAll("\\D", "");
        log.debug("Validando se associado {} já votou na pauta {}", cleanCpf, sessao.getPauta().getId());

        boolean jaVotou = votoRepository.existsBySessaoIdAndCpfAssociado(sessao.getId(), cleanCpf);
        if (jaVotou) {
            throw new VotoDuplicadoException(cpfAssociado, sessao.getPauta().getId());
        }
    }

    @Override
    public int getOrdem() {
        return 2;
    }
}
