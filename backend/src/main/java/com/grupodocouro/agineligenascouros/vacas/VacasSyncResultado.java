package com.grupodocouro.agineligenascouros.vacas;

/** Resumo de uma rodada de sincronizacao de Fornecedor a partir da API das Vacas. */
public record VacasSyncResultado(int criados, int atualizados, int falhas, int totalRecebido) {
}
