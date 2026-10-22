-- Produto comercializado. preco e descricao ficam NULL quando o valor de
-- negócio ainda não foi confirmado pelo dono do projeto (ver README da pasta
-- de migrations) — não devem ser preenchidos com valores inventados.
-- categoria_id é obrigatório; fornecedor_id é opcional (produto pode ainda
-- não ter fornecedor definido).
CREATE TABLE produto (
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome           VARCHAR(150) NOT NULL,
    descricao      VARCHAR(500) NULL,
    preco          DECIMAL(10,2) NULL,
    unidade_medida VARCHAR(20) NULL,
    categoria_id   BIGINT UNSIGNED NOT NULL,
    fornecedor_id  BIGINT UNSIGNED NULL,
    ativo          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_produto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_produto_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedor (id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_produto_preco_nao_negativo CHECK (preco IS NULL OR preco >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
