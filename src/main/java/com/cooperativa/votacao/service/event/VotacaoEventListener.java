package com.cooperativa.votacao.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VotacaoEventListener {

    @Async
    @EventListener
    public void onSessaoAberta(SessaoAbertaEvent event) {
        log.info("[AUDITORIA] Sessão aberta: ID={}, Pauta={}, Fechamento={}",
                event.getSessaoId(), event.getPautaTitulo(), event.getDataFechamento());
    }

    @Async
    @EventListener
    public void onVotoComputado(VotoComputadoEvent event) {
        log.info("[AUDITORIA] Voto registrado: VotoID={}, SessaoID={}, PautaID={}, Opcao={}, Horario={}",
                event.getVotoId(), event.getSessaoId(), event.getPautaId(), event.getOpcao(), event.getDataHora());
    }
}
