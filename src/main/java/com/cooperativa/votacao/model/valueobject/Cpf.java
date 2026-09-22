package com.cooperativa.votacao.model.valueobject;

import com.cooperativa.votacao.exception.CpfInvalidoException;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Cpf implements Serializable {

    private String numero;

    protected Cpf() {
        // JPA requirement
    }

    public Cpf(String numero) {
        if (numero == null) {
            throw new CpfInvalidoException("CPF não pode ser nulo");
        }
        String clean = numero.replaceAll("\\D", "");
        if (!isValido(clean)) {
            throw new CpfInvalidoException("CPF inválido: " + numero);
        }
        this.numero = clean;
    }

    public static Cpf of(String numero) {
        return new Cpf(numero);
    }

    @JsonValue
    public String getNumero() {
        return numero;
    }

    public String getFormatado() {
        if (numero == null || numero.length() != 11) {
            return numero;
        }
        return String.format("%s.%s.%s-%s",
                numero.substring(0, 3),
                numero.substring(3, 6),
                numero.substring(6, 9),
                numero.substring(9, 11));
    }

    public static boolean isValido(String cpfClean) {
        if (cpfClean == null || cpfClean.length() != 11) {
            return false;
        }

        if (cpfClean.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpfClean.charAt(i)) * (10 - i);
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 >= 10) {
                digito1 = 0;
            }

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpfClean.charAt(i)) * (11 - i);
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 >= 10) {
                digito2 = 0;
            }

            return Character.getNumericValue(cpfClean.charAt(9)) == digito1
                    && Character.getNumericValue(cpfClean.charAt(10)) == digito2;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cpf other)) return false;
        return Objects.equals(numero, other.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return numero;
    }
}
