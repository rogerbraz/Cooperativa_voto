package com.cooperativa.votacao.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class SessaoAbertaEvent extends ApplicationEvent {

    private final Long sessaoId;
    private final Long pautaId;
    private final String pautaTitulo;
    private final LocalDateTime dataFechamento;

    public SessaoAbertaEvent(Object source, Long sessaoId, Long pautaId, String pautaTitulo, LocalDateTime dataFechamento) {
        super(source);
        this.sessaoId = sessaoId;
        this.pautaId = pautaId;
        this.pautaTitulo = pautaTitulo;
        this.dataFechamento = dataFechamento;
    }
}
