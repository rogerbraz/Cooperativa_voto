package com.cooperativa.votacao.exception;

public class PautaNaoEncontradaException extends BusinessException {
    public PautaNaoEncontradaException(Long id) {
        super(String.format("Pauta com ID %d não foi encontrada", id));
    }
}
