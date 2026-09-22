package com.cooperativa.votacao.service.event;

import com.cooperativa.votacao.model.enums.OpcaoVoto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class VotoComputadoEvent extends ApplicationEvent {

    private final Long votoId;
    private final Long sessaoId;
    private final Long pautaId;
    private final String cpfAssociado;
    private final OpcaoVoto opcao;
    private final LocalDateTime dataHora;

    public VotoComputadoEvent(Object source, Long votoId, Long sessaoId, Long pautaId, String cpfAssociado, OpcaoVoto opcao, LocalDateTime dataHora) {
        super(source);
        this.votoId = votoId;
        this.sessaoId = sessaoId;
        this.pautaId = pautaId;
        this.cpfAssociado = cpfAssociado;
        this.opcao = opcao;
        this.dataHora = dataHora;
    }
}
