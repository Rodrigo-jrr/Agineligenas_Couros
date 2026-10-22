package com.grupodocouro.agineligenascouros.produto;

import com.grupodocouro.agineligenascouros.categoria.Categoria;
import com.grupodocouro.agineligenascouros.estoque.Estoque;
import com.grupodocouro.agineligenascouros.fornecedor.Fornecedor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mapeia a tabela {@code produto} (ver V3__create_table_produto.sql).
 *
 * <p>ATENCAO: {@code preco}, {@code descricao} e {@code unidadeMedida} podem
 * vir NULL no banco hoje (dados reais do catalogo ainda nao confirmados pelo
 * dono do projeto - ver README das migrations). Nao assumir NOT NULL aqui.
 */
@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "preco", precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "unidade_medida", length = 20)
    private String unidadeMedida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @OneToOne(mappedBy = "produto", fetch = FetchType.LAZY)
    private Estoque estoque;
}
