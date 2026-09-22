package com.cooperativa.votacao.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Requisição para abertura de sessão de votação")
public record AbrirSessaoRequest(
        @NotNull(message = "O ID da pauta é obrigatório")
        @Schema(description = "Identificador único da pauta", example = "1")
        Long pautaId,

        @Positive(message = "A duração da sessão deve ser maior que zero")
        @Schema(description = "Duração da sessão em minutos (padrão: 1 minuto se não informado)", example = "1")
        Long duracaoMinutos
) {}
