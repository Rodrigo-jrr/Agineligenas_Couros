package com.grupodocouro.agineligenascouros.produto;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query(
        value = "SELECT p FROM Produto p LEFT JOIN FETCH p.estoque "
            + "WHERE (:categoriaId IS NULL OR p.categoria.id = :categoriaId)",
        countQuery = "SELECT COUNT(p) FROM Produto p "
            + "WHERE (:categoriaId IS NULL OR p.categoria.id = :categoriaId)")
    Page<Produto> buscar(@Param("categoriaId") Long categoriaId, Pageable pageable);

    @Query("SELECT p FROM Produto p LEFT JOIN FETCH p.estoque WHERE p.id = :id")
    Optional<Produto> buscarPorIdComEstoque(@Param("id") Long id);
}
