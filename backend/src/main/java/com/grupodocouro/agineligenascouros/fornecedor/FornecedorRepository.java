package com.grupodocouro.agineligenascouros.fornecedor;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    /**
     * {@code referencia_externa_id} nao tem UNIQUE constraint no banco (ver
     * V2__create_table_fornecedor.sql e o README das migrations) - a
     * aplicacao e responsavel por fazer find-then-upsert, nao ha garantia de
     * unicidade do lado do banco contra concorrencia.
     */
    Optional<Fornecedor> findByTipoAndReferenciaExternaId(TipoFornecedor tipo, String referenciaExternaId);
}
