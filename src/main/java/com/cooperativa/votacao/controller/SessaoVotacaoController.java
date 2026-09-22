package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.model.dto.request.AbrirSessaoRequest;
import com.cooperativa.votacao.model.dto.response.SessaoResponse;
import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.service.SessaoVotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessoes")
@RequiredArgsConstructor
@Tag(name = "Sessões de Votação", description = "Endpoints para abertura e consulta de sessões de votação")
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoService;

    @PostMapping
    @Operation(summary = "Abrir uma nova sessão de votação", description = "Abre uma sessão para receber votos em uma pauta. O tempo de abertura é em minutos (padrão: 1 minuto se não informado).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sessão aberta com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Já existe sessão cadastrada para esta pauta")
    })
    public ResponseEntity<SessaoResponse> abrirSessao(@Valid @RequestBody AbrirSessaoRequest request) {
        SessaoVotacao sessao = sessaoService.abrirSessao(request.pautaId(), request.duracaoMinutos());
        return ResponseEntity.status(HttpStatus.CREATED).body(SessaoResponse.fromEntity(sessao));
    }

    @GetMapping("/pauta/{pautaId}")
    @Operation(summary = "Buscar sessão por ID da pauta", description = "Consulta os dados e status da sessão associada a uma pauta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão encontrada"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    })
    public ResponseEntity<SessaoResponse> buscarPorPautaId(@PathVariable Long pautaId) {
        SessaoVotacao sessao = sessaoService.buscarPorPautaId(pautaId);
        return ResponseEntity.ok(SessaoResponse.fromEntity(sessao));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sessão por ID", description = "Consulta os dados de uma sessão de votação específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão encontrada"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    })
    public ResponseEntity<SessaoResponse> buscarPorId(@PathVariable Long id) {
        SessaoVotacao sessao = sessaoService.buscarPorId(id);
        return ResponseEntity.ok(SessaoResponse.fromEntity(sessao));
    }

    @GetMapping("/abertas")
    @Operation(summary = "Listar sessões ativas no momento", description = "Retorna todas as sessões de votação atualmente abertas.")
    @ApiResponse(responseCode = "200", description = "Lista de sessões abertas retornada")
    public ResponseEntity<List<SessaoResponse>> listarAbertas() {
        List<SessaoResponse> abertas = sessaoService.listarSessoesAbertas().stream()
                .map(SessaoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(abertas);
    }
}
