package com.github.trabalhoengsoftseguro.artificio.config;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Tratamento global e defensivo de exceções para proteger a aplicação contra vazamento de stack traces
 * e garantir degradação elegante e amigável frente a entradas anômalas ou corrompidas.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatusException(ResponseStatusException ex, Model model, HttpServletResponse response) {
        int statusCode = ex.getStatusCode().value();
        response.setStatus(statusCode);

        log.warn("Exceção ResponseStatusException capturada: status={}, razao={}", statusCode, ex.getReason());

        model.addAttribute("status", statusCode);
        model.addAttribute("titulo", statusCode == 404 ? "Recurso Não Encontrado" : "Aviso da Aplicação");
        model.addAttribute("mensagem", ex.getReason() != null ? ex.getReason() : "O recurso solicitado não pôde ser localizado ou processado.");
        return "error";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        log.warn("Falha de conversão de parâmetro/UUID: parametro='{}', valorRecebido='{}'", ex.getName(), ex.getValue());

        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("titulo", "Identificador ou Parâmetro Inválido");
        model.addAttribute("mensagem", "O identificador informado na requisição ('" + ex.getValue() + "') possui formato inválido.");
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model, HttpServletResponse response) {
        boolean isNaoEncontrado = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("não encontrad");
        HttpStatus status = isNaoEncontrado ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        response.setStatus(status.value());

        log.warn("Argumento inválido capturado: {}", ex.getMessage());

        model.addAttribute("status", status.value());
        model.addAttribute("titulo", isNaoEncontrado ? "Registro Não Encontrado" : "Requisição Inválida");
        model.addAttribute("mensagem", ex.getMessage() != null ? ex.getMessage() : "Os parâmetros da requisição são inválidos.");
        return "error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.CONFLICT.value());

        log.warn("Estado ilegal da aplicação capturado: {}", ex.getMessage());

        model.addAttribute("status", HttpStatus.CONFLICT.value());
        model.addAttribute("titulo", "Operação Não Permitida");
        model.addAttribute("mensagem", ex.getMessage() != null ? ex.getMessage() : "A operação solicitada não é válida para o estado atual do registro.");
        return "error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(MaxUploadSizeExceededException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.CONTENT_TOO_LARGE.value());

        log.warn("Tentativa de upload com tamanho excedido: {}", ex.getMessage());

        model.addAttribute("status", HttpStatus.CONTENT_TOO_LARGE.value());
        model.addAttribute("titulo", "Arquivo Muito Grande");
        model.addAttribute("mensagem", "O arquivo enviado excede o limite máximo permitido pela plataforma (5MB).");
        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());

        log.info("Recurso estático ou rota não localizada: {}", ex.getResourcePath());

        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("titulo", "Página Não Encontrada");
        model.addAttribute("mensagem", "A página ou recurso solicitado não existe.");
        return "error";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());

        log.warn("Método HTTP não suportado: {}", ex.getMessage());

        model.addAttribute("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        model.addAttribute("titulo", "Método Não Permitido");
        model.addAttribute("mensagem", "O método HTTP utilizado para acessar este recurso não é suportado.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        log.error("Exceção não tratada capturada pelo GlobalExceptionHandler defensivo:", ex);

        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("titulo", "Instabilidade Inesperada");
        model.addAttribute("mensagem", "Ocorreu um erro interno inesperado ao processar sua requisição. Nossa equipe foi notificada.");
        return "error";
    }
}
