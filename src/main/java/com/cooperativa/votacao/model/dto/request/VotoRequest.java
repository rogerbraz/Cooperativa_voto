package com.cooperativa.votacao.model.dto.request;

import com.cooperativa.votacao.model.enums.OpcaoVoto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Requisição para envio de voto em uma pauta")
public record VotoRequest(
        @NotBlank(message = "O CPF do associado é obrigatório")
        @Schema(description = "CPF do associado votante", example = "19839091069")
        String cpfAssociado,

        @NotNull(message = "A opção de voto é obrigatória ('Sim' ou 'Não')")
        @Schema(description = "Opção de voto escolhida (Sim ou Não)", example = "Sim")
        OpcaoVoto opcao
) {}
