package com.cooperativa.votacao.service.strategy;

import com.cooperativa.votacao.model.entity.SessaoVotacao;

public interface ValidadorVotoStrategy {
    void validar(SessaoVotacao sessao, String cpfAssociado);
    int getOrdem();
}
