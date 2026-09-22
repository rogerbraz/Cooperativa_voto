package com.cooperativa.votacao.repository.dto;

import com.cooperativa.votacao.model.enums.OpcaoVoto;

public interface VotoContagemProjection {
    OpcaoVoto getOpcao();
    Long getTotal();
}
