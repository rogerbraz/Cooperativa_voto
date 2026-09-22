package com.cooperativa.votacao.exception;

public class SessaoJaExisteException extends BusinessException {
    public SessaoJaExisteException(Long pautaId) {
        super(String.format("Já existe uma sessão de votação cadastrada para a pauta %d", pautaId));
    }
}
