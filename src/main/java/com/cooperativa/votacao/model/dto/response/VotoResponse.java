package com.cooperativa.votacao.model.dto.response;

import com.cooperativa.votacao.model.entity.Voto;
import com.cooperativa.votacao.model.enums.OpcaoVoto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Comprovante e confirmação de registro de voto")
public record VotoResponse(
        @Schema(description = "Identificador do voto computado", example = "1")
        Long id,

        @Schema(description = "Identificador da pauta", example = "1")
        Long pautaId,

        @Schema(description = "CPF do associado (mascarado para privacidade)", example = "198.***.***-69")
        String cpfMascarado,

        @Schema(description = "Opção de voto registrada", example = "Sim")
        OpcaoVoto opcao,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(description = "Data e hora em que o voto foi registrado", example = "2026-09-21T10:00:30")
        LocalDateTime dataHora,

        @Schema(description = "Mensagem de sucesso", example = "Voto registrado com sucesso!")
        String mensagem
) {
    public static VotoResponse fromEntity(Voto voto) {
        String cpf = voto.getCpfAssociado();
        String mascarado = cpf;
        if (cpf != null && cpf.length() == 11) {
            mascarado = cpf.substring(0, 3) + ".***.***-" + cpf.substring(9, 11);
        }

        return VotoResponse.builder()
                .id(voto.getId())
                .pautaId(voto.getSessao().getPauta().getId())
                .cpfMascarado(mascarado)
                .opcao(voto.getOpcao())
                .dataHora(voto.getDataHora())
                .mensagem("Voto registrado com sucesso!")
                .build();
    }
}
