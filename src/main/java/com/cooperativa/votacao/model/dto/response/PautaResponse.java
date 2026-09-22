package com.cooperativa.votacao.model.dto.response;

import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.model.enums.StatusSessao;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Dados da pauta")
public record PautaResponse(
        @Schema(description = "Identificador da pauta", example = "1")
        Long id,

        @Schema(description = "Título da pauta", example = "Aprovação do Balanço Financeiro 2026")
        String titulo,

        @Schema(description = "Descrição da pauta", example = "Votação para deliberar sobre a aprovação das contas.")
        String descricao,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Data e hora de criação da pauta", example = "2026-09-21T10:00:00")
        LocalDateTime criadaEm,

        @Schema(description = "Status da sessão de votação associada (caso exista)", example = "ABERTA")
        StatusSessao statusSessao,

        @Schema(description = "Indica se há uma sessão de votação aberta no momento", example = "true")
        boolean sessaoAberta
) {
    public static PautaResponse fromEntity(Pauta pauta) {
        boolean aberta = pauta.getSessao() != null && pauta.getSessao().isAberta();
        StatusSessao status = pauta.getSessao() != null ? pauta.getSessao().getStatus() : null;

        return PautaResponse.builder()
                .id(pauta.getId())
                .titulo(pauta.getTitulo())
                .descricao(pauta.getDescricao())
                .criadaEm(pauta.getCriadaEm())
                .statusSessao(status)
                .sessaoAberta(aberta)
                .build();
    }
}
