package com.grupodocouro.agineligenascouros.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Dispara quando a requisicao chega sem autenticacao valida em /api/v1/**
 * (header ausente ou API Key que nao corresponde a nenhum cliente
 * registrado) -&gt; sempre 401.
 */
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public ApiAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
        throws IOException {
        SecurityErrorWriter.write(
            request, response, objectMapper, HttpStatus.UNAUTHORIZED, "API Key ausente ou invalida.");
    }
}
