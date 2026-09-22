package com.cooperativa.votacao.service.client;

import com.cooperativa.votacao.config.IntegracaoProperties;
import com.cooperativa.votacao.exception.CpfInvalidoException;
import com.cooperativa.votacao.model.enums.StatusAssociadoVoto;
import com.cooperativa.votacao.model.valueobject.Cpf;
import com.cooperativa.votacao.service.client.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInfoClientAdapter implements UserInfoClient {

    private final RestTemplate restTemplate;
    private final IntegracaoProperties properties;
    private final Random random = new Random();

    @Override
    public StatusAssociadoVoto verificarStatusVoto(String cpf) {
        String cleanCpf = cpf.replaceAll("\\D", "");

        // Validação primária estrutural e dos dígitos verificadores (módulo 11)
        if (!Cpf.isValido(cleanCpf)) {
            log.warn("CPF matematicamente inválido: {}", cleanCpf);
            throw new CpfInvalidoException("CPF inválido: " + cpf);
        }

        String url = String.format("%s/%s", properties.getUrl(), cleanCpf);
        log.info("Consultando elegibilidade de voto para CPF {} na URL {}", cleanCpf, url);

        try {
            ResponseEntity<UserInfoResponse> response = restTemplate.getForEntity(url, UserInfoResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Resposta recebida da API externa para CPF {}: {}", cleanCpf, response.getBody().getStatus());
                return response.getBody().getStatus();
            }

            return StatusAssociadoVoto.UNABLE_TO_VOTE;

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("API externa retornou 404 para o CPF {}", cleanCpf);

            if (properties.isMockFallbackEnabled()) {
                StatusAssociadoVoto status = random.nextBoolean() ? StatusAssociadoVoto.ABLE_TO_VOTE : StatusAssociadoVoto.UNABLE_TO_VOTE;
                log.info("[MOCK SIMULATOR] Endpoint Heroku indisponível. Simulação aleatória para CPF válido {}: {}", cleanCpf, status);
                return status;
            }

            throw new CpfInvalidoException("CPF não encontrado ou inválido no sistema externo: " + cpf);

        } catch (HttpClientErrorException | ResourceAccessException e) {
            log.warn("Falha na comunicação com a API externa ({}).", e.getMessage());

            if (properties.isMockFallbackEnabled()) {
                StatusAssociadoVoto status = random.nextBoolean() ? StatusAssociadoVoto.ABLE_TO_VOTE : StatusAssociadoVoto.UNABLE_TO_VOTE;
                log.info("[MOCK SIMULATOR] Simulação aleatória para CPF válido {}: {}", cleanCpf, status);
                return status;
            }

            throw new CpfInvalidoException("Não foi possível validar o CPF junto ao serviço externo: " + e.getMessage());
        }
    }
}
