package com.cooperativa.votacao.service.strategy;

import com.cooperativa.votacao.exception.AssociadoNaoHabilitadoException;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.model.enums.StatusAssociadoVoto;
import com.cooperativa.votacao.service.client.UserInfoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class ValidadorCpfHabilitadoStrategy implements ValidadorVotoStrategy {

    private final UserInfoClient userInfoClient;

    @Override
    public void validar(SessaoVotacao sessao, String cpfAssociado) {
        String cleanCpf = cpfAssociado.replaceAll("\\D", "");
        log.debug("Validando se associado {} tem permissão de voto no serviço externo", cleanCpf);

        StatusAssociadoVoto status = userInfoClient.verificarStatusVoto(cleanCpf);
        if (status == StatusAssociadoVoto.UNABLE_TO_VOTE) {
            throw new AssociadoNaoHabilitadoException(cpfAssociado);
        }
    }

    @Override
    public int getOrdem() {
        return 3;
    }
}
