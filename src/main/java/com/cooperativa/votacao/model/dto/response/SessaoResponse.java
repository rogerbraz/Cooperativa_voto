package com.cooperativa.votacao.model.dto.response;

import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.model.enums.StatusSessao;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Dados da sessão de votação")
public record SessaoResponse(
        @Schema(description = "Identificador da sessão", example = "1")
        Long id,

        @Schema(description = "Identificador da pauta associada", example = "1")
        Long pautaId,

        @Schema(description = "Título da pauta associada", example = "Aprovação do Balanço Financeiro 2026")
        String pautaTitulo,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Data e hora de abertura da sessão", example = "2026-09-21T10:00:00")
        LocalDateTime dataAbertura,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Data e hora de fechamento da sessão", example = "2026-09-21T10:01:00")
        LocalDateTime dataFechamento,

        @Schema(description = "Status da sessão", example = "ABERTA")
        StatusSessao status,

        @Schema(description = "Indica se a sessão está aberta para receber votos", example = "true")
        boolean aberta,

        @Schema(description = "Segundos restantes para o encerramento da votação", example = "58")
        long segundosRestantes
) {
    public static SessaoResponse fromEntity(SessaoVotacao sessao) {
        return SessaoResponse.builder()
                .id(sessao.getId())
                .pautaId(sessao.getPauta().getId())
                .pautaTitulo(sessao.getPauta().getTitulo())
                .dataAbertura(sessao.getDataAbertura())
                .dataFechamento(sessao.getDataFechamento())
                .status(sessao.getStatus())
                .aberta(sessao.isAberta())
                .segundosRestantes(sessao.getSegundosRestantes())
                .build();
    }
}
