package com.grupodocouro.agineligenascouros.common.exception;

import java.time.Instant;

/**
 * Formato padronizado de erro de toda a API.
 * Nunca deve carregar dados sensiveis (API keys, stack traces, etc.).
 */
public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path) {

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path);
    }
}
