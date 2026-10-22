package com.grupodocouro.agineligenascouros.estoque;

import com.grupodocouro.agineligenascouros.produto.Produto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Mapeia a tabela {@code estoque} (ver V4__create_table_estoque.sql).
 * Relacao 1:1 com {@code produto} (unique + FK cascade on delete no banco).
 */
@Entity
@Table(name = "estoque")
@Getter
@Setter
@NoArgsConstructor
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    private Produto produto;

    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    /**
     * TIMESTAMP do MySQL nao tem WITH TIME ZONE nativo - forcamos
     * {@code SqlTypes.TIMESTAMP} (em vez do default TIMESTAMP_WITH_TIMEZONE
     * que Hibernate 6 infere para OffsetDateTime) para o valor continuar
     * sendo lido/escrito como TIMESTAMP comum, normalizado para UTC via
     * {@code hibernate.jdbc.time_zone: UTC} (ver application.yml). O offset
     * de exibicao (ex. America/Sao_Paulo) e aplicado na camada de DTO, nao
     * aqui - a entidade representa o instante em si.
     */
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @Column(name = "atualizado_em", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime atualizadoEm;
}
