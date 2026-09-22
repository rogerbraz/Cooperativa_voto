package com.cooperativa.votacao.exception;

public class AssociadoNaoHabilitadoException extends BusinessException {
    public AssociadoNaoHabilitadoException(String cpf) {
        super(String.format("O associado com CPF %s não está habilitado para votar (UNABLE_TO_VOTE)", cpf));
    }
}
