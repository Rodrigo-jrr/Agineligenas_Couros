package com.grupodocouro.agineligenascouros.fornecedor;

/**
 * Espelha o CHECK {@code chk_fornecedor_tipo} de V2__create_table_fornecedor.sql
 * (coluna VARCHAR, nao ENUM nativo do MySQL - ver README das migrations).
 */
public enum TipoFornecedor {
    INTERNO,
    VACAS
}
