package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.model.dto.request.NovaPautaRequest;
import com.cooperativa.votacao.model.dto.response.PautaResponse;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.service.PautaService;
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
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas", description = "Endpoints para gerenciamento de pautas da assembleia")
public class PautaController {

    private final PautaService pautaService;

    @PostMapping
    @Operation(summary = "Cadastrar uma nova pauta", description = "Cria uma nova pauta para ser submetida a votação em assembleia.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pauta cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
    })
    public ResponseEntity<PautaResponse> cadastrar(@Valid @RequestBody NovaPautaRequest request) {
        Pauta pauta = pautaService.cadastrarPauta(request.titulo(), request.descricao());
        return ResponseEntity.status(HttpStatus.CREATED).body(PautaResponse.fromEntity(pauta));
    }

    @GetMapping
    @Operation(summary = "Listar todas as pautas", description = "Retorna a listagem completa de pautas cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista de pautas retornada com sucesso")
    public ResponseEntity<List<PautaResponse>> listarTodas() {
        List<PautaResponse> pautas = pautaService.listarTodas().stream()
                .map(PautaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(pautas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID", description = "Retorna os detalhes de uma pauta específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pauta encontrada"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable Long id) {
        Pauta pauta = pautaService.buscarPorId(id);
        return ResponseEntity.ok(PautaResponse.fromEntity(pauta));
    }
}
