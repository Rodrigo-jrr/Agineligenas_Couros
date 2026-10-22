package com.grupodocouro.agineligenascouros.fornecedor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mapeia a tabela {@code fornecedor} (ver V2__create_table_fornecedor.sql).
 * Sem endpoints proprios nesta etapa - existe como relacionamento opcional
 * de {@code produto} (fornecedorId exposto no contrato de resposta).
 */
@Entity
@Table(name = "fornecedor")
@Getter
@Setter
@NoArgsConstructor
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoFornecedor tipo;

    @Column(name = "referencia_externa_id", length = 100)
    private String referenciaExternaId;
}
