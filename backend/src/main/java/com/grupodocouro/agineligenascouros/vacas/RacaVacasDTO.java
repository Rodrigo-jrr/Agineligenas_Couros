package com.grupodocouro.agineligenascouros.vacas;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa uma raca no formato da API das Vacas (contrato delas, NAO
 * nosso - ver GET /api/parceiro/racas e /api/parceiro/racas/{id}).
 *
 * <p>{@code preco} fica como {@code String} de proposito, exatamente como
 * eles mandam (ex.: {@code "150.00"}) - NAO e numero no contrato deles. Hoje
 * nao usamos o preco pra nada na sincronizacao (so nome e id entram em
 * Fornecedor), entao nao ha necessidade de parsear pra BigDecimal e correr
 * risco de erro de parsing numa sincronizacao que, por ora, nem usa esse
 * valor. Se um consumidor futuro precisar do preco como numero, parsear no
 * ponto de uso.
 */
public record RacaVacasDTO(
    Long id,
    String nome,
    @JsonProperty("quantidade_disponivel") Integer quantidadeDisponivel,
    String preco) {
}
