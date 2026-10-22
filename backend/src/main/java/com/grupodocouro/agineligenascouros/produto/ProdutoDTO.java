package com.grupodocouro.agineligenascouros.produto;

import java.math.BigDecimal;

/**
 * Representacao publica de um produto. Nunca expor a entidade JPA direto no
 * controller - preco, descricao e unidadeMedida sao opcionais porque podem
 * vir NULL no banco (ver README das migrations).
 */
public record ProdutoDTO(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    Integer quantidadeDisponivel,
    String unidadeMedida,
    Long fornecedorId) {

    public static ProdutoDTO from(Produto produto) {
        Integer quantidade = produto.getEstoque() != null
            ? produto.getEstoque().getQuantidadeDisponivel()
            : null;
        Long fornecedorId = produto.getFornecedor() != null
            ? produto.getFornecedor().getId()
            : null;

        return new ProdutoDTO(
            produto.getId(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getPreco(),
            quantidade,
            produto.getUnidadeMedida(),
            fornecedorId);
    }
}
