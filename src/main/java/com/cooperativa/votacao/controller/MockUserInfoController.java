package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.model.dto.response.CpfMockResponse;
import com.cooperativa.votacao.model.enums.StatusAssociadoVoto;
import com.cooperativa.votacao.model.valueobject.Cpf;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Slf4j
@RestController
@RequestMapping({"/mock/users", "/users"})
@Tag(name = "Mock do Sistema Externo", description = "Simulador local da API externa de verificação de CPF (Tarefa Bônus 1)")
public class MockUserInfoController {

    private final Random random = new Random();

    @GetMapping("/{cpf}")
    @Operation(summary = "Simular verificação de CPF externa", description = "Retorna 404 se o CPF for inválido, ou retorna aleatoriamente ABLE_TO_VOTE / UNABLE_TO_VOTE se o CPF for válido.")
    public ResponseEntity<?> verificarCpfMock(@PathVariable String cpf) {
        String cleanCpf = cpf.replaceAll("\\D", "");

        if (!Cpf.isValido(cleanCpf)) {
            log.info("[MOCK EXTERNO] CPF {} inválido -> Retornando HTTP 404", cleanCpf);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Se CPF for válido, retorna aleatoriamente ABLE_TO_VOTE ou UNABLE_TO_VOTE (conforme especificação da Tarefa Bônus 1)
        StatusAssociadoVoto status = random.nextBoolean() ? StatusAssociadoVoto.ABLE_TO_VOTE : StatusAssociadoVoto.UNABLE_TO_VOTE;
        log.info("[MOCK EXTERNO] CPF {} válido -> Retornando {}", cleanCpf, status);

        return ResponseEntity.ok(new CpfMockResponse(status));
    }
}
