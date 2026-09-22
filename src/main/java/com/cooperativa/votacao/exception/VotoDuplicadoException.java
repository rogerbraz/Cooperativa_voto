package com.cooperativa.votacao.exception;

public class VotoDuplicadoException extends BusinessException {
    public VotoDuplicadoException(String cpf, Long pautaId) {
        super(String.format("O associado com CPF %s já registrou seu voto na pauta %d", cpf, pautaId));
    }
}
