package com.grupodocouro.agineligenascouros.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupodocouro.agineligenascouros.common.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Escreve respostas de erro de autenticacao/autorizacao no mesmo formato
 * ApiError usado pelo resto da API (ver GlobalExceptionHandler).
 */
final class SecurityErrorWriter {

    private SecurityErrorWriter() {
    }

    static void write(
        HttpServletRequest request, HttpServletResponse response, ObjectMapper objectMapper,
        HttpStatus status, String message) throws IOException {

        ApiError body = ApiError.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
