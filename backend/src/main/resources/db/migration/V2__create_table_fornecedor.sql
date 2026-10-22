-- Fornecedor de produtos. Pode ser interno (cadastro próprio) ou vindo da API
-- externa das Vacas, caso em que referencia_externa_id guarda o id do registro
-- de origem para permitir reconciliação/sincronização futura.
CREATE TABLE fornecedor (
    id                    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome                  VARCHAR(150) NOT NULL,
    tipo                  VARCHAR(20) NOT NULL,
    referencia_externa_id VARCHAR(100) NULL,
    CONSTRAINT chk_fornecedor_tipo CHECK (tipo IN ('INTERNO', 'VACAS')),
    INDEX idx_fornecedor_referencia_externa_id (referencia_externa_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
