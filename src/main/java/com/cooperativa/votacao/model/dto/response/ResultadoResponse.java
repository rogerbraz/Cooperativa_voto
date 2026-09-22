package com.cooperativa.votacao.model.dto.response;

import com.cooperativa.votacao.model.enums.ResultadoVotacao;
import com.cooperativa.votacao.model.enums.StatusSessao;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Resultado apurado da votação de uma pauta")
public record ResultadoResponse(
        @Schema(description = "Identificador da pauta", example = "1")
        Long pautaId,

        @Schema(description = "Título da pauta", example = "Aprovação do Balanço Financeiro 2026")
        String pautaTitulo,

        @Schema(description = "Identificador da sessão", example = "1")
        Long sessaoId,

        @Schema(description = "Status atual da sessão", example = "ENCERRADA")
        StatusSessao statusSessao,

        @Schema(description = "Indica se a sessão ainda está em andamento", example = "false")
        boolean sessaoAberta,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Data e hora de abertura", example = "2026-09-21T10:00:00")
        LocalDateTime dataAbertura,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Data e hora de fechamento", example = "2026-09-21T10:01:00")
        LocalDateTime dataFechamento,

        @Schema(description = "Total geral de votos computados", example = "150000")
        long totalVotos,

        @Schema(description = "Total de votos 'Sim'", example = "120000")
        long votosSim,

        @Schema(description = "Total de votos 'Não'", example = "30000")
        long votosNao,

        @Schema(description = "Resultado final deliberado", example = "APROVADA")
        ResultadoVotacao resultado
) {}
