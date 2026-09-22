package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.model.dto.request.VotoRequest;
import com.cooperativa.votacao.model.dto.response.VotoResponse;
import com.cooperativa.votacao.model.entity.Voto;
import com.cooperativa.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Votos", description = "Endpoints para registro de votos de associados nas pautas")
public class VotoController {

    private final VotoService votoService;

    @PostMapping("/pautas/{pautaId}/votos")
    @Operation(summary = "Registrar voto em uma pauta", description = "Registra o voto ('Sim' ou 'Não') do associado identificado por CPF. Cada associado pode votar apenas uma vez por pauta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Voto computado com sucesso"),
            @ApiResponse(responseCode = "400", description = "CPF inválido ou campos inconsistentes"),
            @ApiResponse(responseCode = "403", description = "Associado não habilitado para votar (UNABLE_TO_VOTE)"),
            @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada"),
            @ApiResponse(responseCode = "409", description = "Voto duplicado - associado já votou nesta pauta"),
            @ApiResponse(responseCode = "422", description = "Sessão de votação já está encerrada")
    })
    public ResponseEntity<VotoResponse> votar(
            @PathVariable Long pautaId,
            @Valid @RequestBody VotoRequest request) {
        Voto voto = votoService.registrarVoto(pautaId, request.cpfAssociado(), request.opcao());
        return ResponseEntity.status(HttpStatus.CREATED).body(VotoResponse.fromEntity(voto));
    }
}
