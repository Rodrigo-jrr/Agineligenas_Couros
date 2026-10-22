package com.grupodocouro.agineligenascouros.produto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/produtos")
@Validated
@Tag(name = "Produtos", description = "Consulta de produtos do catalogo")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUTO_READ')")
    @Operation(summary = "Lista produtos, paginavel e filtravel por categoria")
    public Page<ProdutoDTO> listar(
        @Parameter(description = "Filtra pelo id da categoria") @RequestParam(required = false) Long categoriaId,
        @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return produtoService.listar(categoriaId, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUTO_READ')")
    @Operation(summary = "Busca um produto pelo id")
    public ProdutoDTO buscarPorId(@PathVariable @Positive Long id) {
        return produtoService.buscarPorId(id);
    }
}
