package com.cooperativa.votacao.exception;

public class SessaoEncerradaException extends BusinessException {
    public SessaoEncerradaException(Long sessaoId) {
        super(String.format("A sessão de votação %d já está encerrada", sessaoId));
    }
}
