package com.grupodocouro.agineligenascouros.estoque;

import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Representacao publica do estoque de um produto.
 *
 * <p>{@code atualizadoEm} sai sempre no horario de Brasilia
 * ({@code America/Sao_Paulo}) com offset explicito no ISO 8601 (ex.:
 * {@code 2026-10-14T15:30:00-03:00}), independente de em que zona o valor
 * estava armazenado - ver {@link Estoque#getAtualizadoEm()}.
 */
public record EstoqueDTO(Long produtoId, Integer quantidadeDisponivel, OffsetDateTime atualizadoEm) {

    private static final ZoneId BRASIL = ZoneId.of("America/Sao_Paulo");

    public static EstoqueDTO from(Estoque estoque) {
        OffsetDateTime atualizadoEmBrasil = estoque.getAtualizadoEm().atZoneSameInstant(BRASIL).toOffsetDateTime();

        return new EstoqueDTO(estoque.getProduto().getId(), estoque.getQuantidadeDisponivel(), atualizadoEmBrasil);
    }
}
