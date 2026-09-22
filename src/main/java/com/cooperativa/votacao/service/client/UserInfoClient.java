package com.cooperativa.votacao.service.client;

import com.cooperativa.votacao.model.enums.StatusAssociadoVoto;

public interface UserInfoClient {
    StatusAssociadoVoto verificarStatusVoto(String cpf);
}
