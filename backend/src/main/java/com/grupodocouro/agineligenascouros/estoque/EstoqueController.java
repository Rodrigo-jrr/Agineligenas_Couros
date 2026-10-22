package com.grupodocouro.agineligenascouros.estoque;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/estoque")
@Validated
@Tag(name = "Estoque", description = "Consulta de estoque por produto")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_READ')")
    @Operation(summary = "Busca o estoque de um produto pelo id do produto")
    public EstoqueDTO buscarPorProdutoId(@PathVariable @Positive Long id) {
        return estoqueService.buscarPorProdutoId(id);
    }
}
