package com.cooperativa.votacao.service.strategy;

import com.cooperativa.votacao.exception.SessaoEncerradaException;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@Order(1)
public class ValidadorSessaoAbertaStrategy implements ValidadorVotoStrategy {

    @Override
    public void validar(SessaoVotacao sessao, String cpfAssociado) {
        log.debug("Validando se sessão {} está aberta", sessao.getId());
        LocalDateTime now = LocalDateTime.now();

        if (sessao.isExpirada(now)) {
            sessao.encerrar();
            throw new SessaoEncerradaException(sessao.getId());
        }

        if (!sessao.isAberta()) {
            throw new SessaoEncerradaException(sessao.getId());
        }
    }

    @Override
    public int getOrdem() {
        return 1;
    }
}
