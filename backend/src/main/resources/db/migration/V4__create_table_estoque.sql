-- Estoque: relação 1:1 com produto (um registro de estoque por produto).
-- ON DELETE CASCADE garante que estoque nunca fica órfão se o produto for removido.
CREATE TABLE estoque (
    id                    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    produto_id            BIGINT UNSIGNED NOT NULL,
    quantidade_disponivel INT NOT NULL DEFAULT 0,
    atualizado_em         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_estoque_produto_id UNIQUE (produto_id),
    CONSTRAINT fk_estoque_produto FOREIGN KEY (produto_id) REFERENCES produto (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_estoque_quantidade_nao_negativa CHECK (quantidade_disponivel >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
