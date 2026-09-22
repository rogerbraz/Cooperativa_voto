package com.cooperativa.votacao.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PautaNaoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> handlePautaNaoEncontrada(PautaNaoEncontradaException ex, HttpServletRequest request) {
        log.warn("Pauta não encontrada: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(SessaoNaoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> handleSessaoNaoEncontrada(SessaoNaoEncontradaException ex, HttpServletRequest request) {
        log.warn("Sessão não encontrada: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(SessaoJaExisteException.class)
    public ResponseEntity<ApiErrorResponse> handleSessaoJaExiste(SessaoJaExisteException ex, HttpServletRequest request) {
        log.warn("Sessão já existente: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(VotoDuplicadoException.class)
    public ResponseEntity<ApiErrorResponse> handleVotoDuplicado(VotoDuplicadoException ex, HttpServletRequest request) {
        log.warn("Voto duplicado detectado: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(SessaoEncerradaException.class)
    public ResponseEntity<ApiErrorResponse> handleSessaoEncerrada(SessaoEncerradaException ex, HttpServletRequest request) {
        log.warn("Tentativa de voto em sessão encerrada: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AssociadoNaoHabilitadoException.class)
    public ResponseEntity<ApiErrorResponse> handleAssociadoNaoHabilitado(AssociadoNaoHabilitadoException ex, HttpServletRequest request) {
        log.warn("Associado não habilitado: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CpfInvalidoException.class)
    public ResponseEntity<ApiErrorResponse> handleCpfInvalido(CpfInvalidoException ex, HttpServletRequest request) {
        log.warn("CPF inválido informado: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Erro de negócio: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Erro de validação de argumentos: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Falha na validação dos campos da requisição")
                .path(request.getRequestURI())
                .fieldErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Corpo da requisição ilegível ou inválido: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Formato da requisição inválido ou valores não permitidos", request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Erro interno não tratado no servidor", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado. Tente novamente mais tarde.", request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
