package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.model.dto.response.ResultadoResponse;
import com.cooperativa.votacao.service.ResultadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/resultado")
@RequiredArgsConstructor
@Tag(name = "Resultados", description = "Endpoints para apuração e contabilização dos votos das pautas")
public class ResultadoController {

    private final ResultadoService resultadoService;

    @GetMapping
    @Operation(summary = "Contabilizar e obter o resultado da votação da pauta", description = "Retorna a apuração dos votos (Sim, Não, Total e Resultado final aprovada/rejeitada/empate).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado apurado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada")
    })
    public ResponseEntity<ResultadoResponse> obterResultado(@PathVariable Long pautaId) {
        ResultadoResponse resultado = resultadoService.apurarResultado(pautaId);
        return ResponseEntity.ok(resultado);
    }
}
