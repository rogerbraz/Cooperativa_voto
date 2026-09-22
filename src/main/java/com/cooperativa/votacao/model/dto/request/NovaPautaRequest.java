package com.cooperativa.votacao.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para cadastro de nova pauta")
public record NovaPautaRequest(
        @NotBlank(message = "O título da pauta é obrigatório")
        @Size(min = 3, max = 255, message = "O título deve ter entre 3 e 255 caracteres")
        @Schema(description = "Título descritivo da pauta", example = "Aprovação do Balanço Financeiro 2026")
        String titulo,

        @Size(max = 1000, message = "A descrição não pode ultrapassar 1000 caracteres")
        @Schema(description = "Descrição detalhada da pauta", example = "Votação para deliberar sobre a aprovação das contas.")
        String descricao
) {}
