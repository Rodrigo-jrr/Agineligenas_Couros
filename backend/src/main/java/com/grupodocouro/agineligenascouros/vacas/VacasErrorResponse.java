package com.grupodocouro.agineligenascouros.vacas;

/**
 * Formato de erro da API das Vacas - diferente do nosso {@code ApiError}:
 * so um campo {@code detail} com a mensagem. Usado so pra enriquecer logs
 * (nunca repassado como resposta nossa).
 */
public record VacasErrorResponse(String detail) {
}
