package com.cooperativa.votacao.model.valueobject;

import com.cooperativa.votacao.exception.CpfInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CpfTest {

    @Test
    @DisplayName("Deve criar CPF válido com pontuação ou apenas números")
    void deveCriarCpfValido() {
        Cpf cpf1 = new Cpf("19839091069");
        Cpf cpf2 = new Cpf("198.390.910-69");

        assertThat(cpf1.getNumero()).isEqualTo("19839091069");
        assertThat(cpf2.getNumero()).isEqualTo("19839091069");
        assertThat(cpf1).isEqualTo(cpf2);
        assertThat(cpf1.getFormatado()).isEqualTo("198.390.910-69");
    }

    @ParameterizedTest
    @ValueSource(strings = {"11111111111", "00000000000", "12345678901", "abc", "123"})
    @DisplayName("Deve lançar exceção para CPFs inválidos ou com dígitos repetidos")
    void deveLancarExcecaoParaCpfInvalido(String cpfInvalido) {
        assertThatThrownBy(() -> new Cpf(cpfInvalido))
                .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF nulo")
    void deveLancarExcecaoParaCpfNulo() {
        assertThatThrownBy(() -> new Cpf(null))
                .isInstanceOf(CpfInvalidoException.class);
    }
}
