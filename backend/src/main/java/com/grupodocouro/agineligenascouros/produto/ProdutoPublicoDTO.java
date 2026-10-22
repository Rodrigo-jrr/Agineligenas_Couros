package com.grupodocouro.agineligenascouros.produto;

import java.math.BigDecimal;

/**
 * Representacao publica (landing page, sem autenticacao) de um produto.
 *
 * <p>Deliberadamente NAO inclui {@code quantidadeDisponivel} (o numero exato
 * de estoque e um dado comercial sensivel) nem {@code fornecedorId} (detalhe
 * de relacionamento B2B) - so a disponibilidade qualitativa. O numero exato
 * de estoque continua disponivel normalmente em {@code GET /api/v1/estoque/{id}}
 * para parceiros B2B autenticados (ex.: Sapatos).
 */
public record ProdutoPublicoDTO(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    String unidadeMedida,
    boolean disponivel) {

    public static ProdutoPublicoDTO from(ProdutoDTO produto) {
        boolean disponivel = produto.quantidadeDisponivel() != null && produto.quantidadeDisponivel() > 0;

        return new ProdutoPublicoDTO(
            produto.id(),
            produto.nome(),
            produto.descricao(),
            produto.preco(),
            produto.unidadeMedida(),
            disponivel);
    }
}
