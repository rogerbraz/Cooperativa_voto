package com.cooperativa.votacao.exception;

public class SessaoNaoEncontradaException extends BusinessException {
    public SessaoNaoEncontradaException(Long pautaId) {
        super(String.format("Sessão de votação para a pauta %d não foi encontrada", pautaId));
    }
}
