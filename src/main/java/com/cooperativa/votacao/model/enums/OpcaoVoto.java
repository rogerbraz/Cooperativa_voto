package com.cooperativa.votacao.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OpcaoVoto {
    SIM("Sim"),
    NAO("Não");

    private final String descricao;

    OpcaoVoto(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static OpcaoVoto fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Opção de voto não pode ser nula ou vazia");
        }
        String normalized = value.trim().toUpperCase();
        if (normalized.equals("SIM") || normalized.equals("S") || normalized.equals("YES")) {
            return SIM;
        }
        if (normalized.equals("NAO") || normalized.equals("NÃO") || normalized.equals("N") || normalized.equals("NO")) {
            return NAO;
        }
        throw new IllegalArgumentException("Opção de voto inválida: " + value + ". Opções válidas: Sim ou Não");
    }
}
