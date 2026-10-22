package com.grupodocouro.agineligenascouros.produto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Catalogo publico de produtos, usado pela landing page (React, sem login).
 * Contrato separado de {@link ProdutoController}: {@code /api/v1/produtos}
 * continua sendo o contrato B2B protegido por API Key para Sapatos/Vacas,
 * sem alteracao. Aqui reaproveitamos {@link ProdutoService} (mesma regra de
 * consulta) mas convertemos para {@link ProdutoPublicoDTO} antes de
 * responder - sem autenticacao, o numero exato de estoque
 * ({@code quantidadeDisponivel}) nao pode ser exposto (dado comercial
 * sensivel), por isso so uma disponibilidade qualitativa e exibida aqui. O
 * numero exato continua em {@code GET /api/v1/estoque/{id}}, protegido por API
 * Key.
 *
 * <p>{@code /api/v1/public/**} e excluido do {@code ApiKeyAuthenticationFilter}
 * em {@code SecurityConfig} (cadeia propria, permitAll, avaliada antes da
 * cadeia que protege o restante de {@code /api/v1/**}).
 */
@RestController
@RequestMapping("/api/v1/public/produtos")
@Validated
@Tag(name = "Produtos (publico)", description = "Catalogo publico de produtos - sem autenticacao, usado pela landing page")
@SecurityRequirements
public class PublicProdutoController {

    private final ProdutoService produtoService;

    public PublicProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    @Operation(summary = "Lista produtos do catalogo publico (sem autenticacao, sem filtro)")
    public Page<ProdutoPublicoDTO> listar(@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return produtoService.listar(null, pageable).map(ProdutoPublicoDTO::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto do catalogo publico pelo id (sem autenticacao)")
    public ProdutoPublicoDTO buscarPorId(@PathVariable @Positive Long id) {
        return ProdutoPublicoDTO.from(produtoService.buscarPorId(id));
    }
}
