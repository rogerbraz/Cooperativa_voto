package com.cooperativa.votacao.service;

import com.cooperativa.votacao.model.enums.StatusAssociadoVoto;
import com.cooperativa.votacao.service.client.UserInfoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoClient userInfoClient;

    public StatusAssociadoVoto verificarElegibilidade(String cpf) {
        return userInfoClient.verificarStatusVoto(cpf);
    }
}
