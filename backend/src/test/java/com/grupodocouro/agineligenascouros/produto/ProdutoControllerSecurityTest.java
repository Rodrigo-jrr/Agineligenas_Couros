package com.grupodocouro.agineligenascouros.produto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.grupodocouro.agineligenascouros.common.config.OpenApiConfig;
import com.grupodocouro.agineligenascouros.common.exception.GlobalExceptionHandler;
import com.grupodocouro.agineligenascouros.common.exception.ResourceNotFoundException;
import com.grupodocouro.agineligenascouros.common.security.ApiAccessDeniedHandler;
import com.grupodocouro.agineligenascouros.common.security.ApiAuthenticationEntryPoint;
import com.grupodocouro.agineligenascouros.common.security.ApiKeyAuthenticationFilter;
import com.grupodocouro.agineligenascouros.common.security.ApiSecurityProperties;
import com.grupodocouro.agineligenascouros.common.security.SecurityConfig;
import com.grupodocouro.agineligenascouros.estoque.EstoqueController;
import com.grupodocouro.agineligenascouros.estoque.EstoqueDTO;
import com.grupodocouro.agineligenascouros.estoque.EstoqueService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Exercita o mecanismo de API Key (401/403/200) e o contrato de resposta dos
 * endpoints de produto/estoque, sem precisar de banco de dados (service
 * mockado). A validacao fim a fim contra o MySQL real com o seed foi feita
 * manualmente - ver backend/README.md.
 */
@WebMvcTest({ProdutoController.class, EstoqueController.class, PublicProdutoController.class})
@Import({SecurityConfig.class, ApiAuthenticationEntryPoint.class, ApiAccessDeniedHandler.class,
    GlobalExceptionHandler.class, OpenApiConfig.class})
@EnableConfigurationProperties(ApiSecurityProperties.class)
@TestPropertySource(properties = {
    "app.security.clients[0].name=sapatos",
    "app.security.clients[0].key=test-sapatos-key",
    "app.security.clients[0].authorities[0]=PRODUTO_READ",
    "app.security.clients[0].authorities[1]=ESTOQUE_READ",
    "app.security.clients[1].name=vacas",
    "app.security.clients[1].key=test-vacas-key",
    "app.security.clients[1].authorities[0]=PRODUTO_READ"
})
class ProdutoControllerSecurityTest {

    private static final String HEADER = ApiKeyAuthenticationFilter.API_KEY_HEADER;
    private static final String SAPATOS_KEY = "test-sapatos-key";
    private static final String VACAS_KEY = "test-vacas-key";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @MockBean
    private EstoqueService estoqueService;

    @Test
    void semApiKeyRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/produtos"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void apiKeyInvalidaRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/produtos").header(HEADER, "chave-invalida"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void vacasSemAuthorityEstoqueRetorna403EmEstoque() throws Exception {
        mockMvc.perform(get("/api/v1/estoque/1").header(HEADER, VACAS_KEY))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void vacasComAuthorityProdutoReadRetorna200EmProdutos() throws Exception {
        ProdutoDTO produto = new ProdutoDTO(1L, "Couro malhado", null, null, 1, null, null);
        given(produtoService.buscarPorId(1L)).willReturn(produto);
        Page<ProdutoDTO> pagina = new PageImpl<>(java.util.List.of(produto));
        given(produtoService.listar(isNull(), any(Pageable.class))).willReturn(pagina);

        mockMvc.perform(get("/api/v1/produtos/1").header(HEADER, VACAS_KEY))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
        mockMvc.perform(get("/api/v1/produtos").header(HEADER, VACAS_KEY))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void sapatosContinuaComAcessoCompletoAposAdicionarVacas() throws Exception {
        given(estoqueService.buscarPorProdutoId(1L)).willReturn(new EstoqueDTO(1L, 1, null));

        mockMvc.perform(get("/api/v1/produtos").header(HEADER, SAPATOS_KEY))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/estoque/1").header(HEADER, SAPATOS_KEY))
            .andExpect(status().isOk());
    }

    @Test
    void apiKeyValidaComPermissaoRetorna200ESemVazarNulosNoContrato() throws Exception {
        ProdutoDTO comCamposNulos = new ProdutoDTO(1L, "Couro malhado", null, null, 1, null, null);
        given(produtoService.buscarPorId(1L)).willReturn(comCamposNulos);

        mockMvc.perform(get("/api/v1/produtos/1").header(HEADER, SAPATOS_KEY))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Couro malhado"))
            .andExpect(jsonPath("$.quantidadeDisponivel").value(1))
            .andExpect(jsonPath("$.descricao").doesNotExist())
            .andExpect(jsonPath("$.preco").doesNotExist())
            .andExpect(jsonPath("$.unidadeMedida").doesNotExist())
            .andExpect(jsonPath("$.fornecedorId").doesNotExist());
    }

    @Test
    void apiKeyValidaComCamposPreenchidosRetorna200() throws Exception {
        ProdutoDTO completo = new ProdutoDTO(2L, "Couro premium", "Alta qualidade", new BigDecimal("99.90"), 5, "m2", 10L);
        given(produtoService.buscarPorId(2L)).willReturn(completo);

        mockMvc.perform(get("/api/v1/produtos/2").header(HEADER, SAPATOS_KEY))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.preco").value(99.90))
            .andExpect(jsonPath("$.fornecedorId").value(10));
    }

    @Test
    void produtoInexistenteRetorna404ComFormatoApiError() throws Exception {
        given(produtoService.buscarPorId(999L)).willThrow(new ResourceNotFoundException("Produto nao encontrado: 999"));

        mockMvc.perform(get("/api/v1/produtos/999").header(HEADER, SAPATOS_KEY))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Produto nao encontrado: 999"));
    }

    @Test
    void listaProdutosPaginadosEFiltraveisPorCategoria() throws Exception {
        ProdutoDTO produto = new ProdutoDTO(1L, "Couro malhado", null, null, 1, null, null);
        Page<ProdutoDTO> pagina = new PageImpl<>(java.util.List.of(produto));
        given(produtoService.listar(eq(5L), any(Pageable.class))).willReturn(pagina);

        mockMvc.perform(get("/api/v1/produtos").queryParam("categoriaId", "5").header(HEADER, SAPATOS_KEY))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void idInvalidoNaBuscaDeProdutoRetorna400() throws Exception {
        mockMvc.perform(get("/api/v1/produtos/-1").header(HEADER, SAPATOS_KEY))
            .andExpect(status().isBadRequest());
    }

    @Test
    void catalogoPublicoListaSemApiKey() throws Exception {
        ProdutoDTO produto = new ProdutoDTO(1L, "Couro malhado", "Descricao", new BigDecimal("10.00"), 3, "m2", 7L);
        Page<ProdutoDTO> pagina = new PageImpl<>(java.util.List.of(produto));
        given(produtoService.listar(isNull(), any(Pageable.class))).willReturn(pagina);

        mockMvc.perform(get("/api/v1/public/produtos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].disponivel").value(true))
            .andExpect(jsonPath("$.content[0].quantidadeDisponivel").doesNotExist())
            .andExpect(jsonPath("$.content[0].fornecedorId").doesNotExist());
    }

    @Test
    void catalogoPublicoBuscaPorIdSemApiKeyNaoExpoeQuantidadeExataDeEstoque() throws Exception {
        ProdutoDTO produto = new ProdutoDTO(1L, "Couro malhado", null, null, 3, null, null);
        given(produtoService.buscarPorId(1L)).willReturn(produto);

        mockMvc.perform(get("/api/v1/public/produtos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.disponivel").value(true))
            .andExpect(jsonPath("$.quantidadeDisponivel").doesNotExist());
    }

    @Test
    void catalogoPublicoComEstoqueZeradoMarcaComoIndisponivel() throws Exception {
        ProdutoDTO produto = new ProdutoDTO(1L, "Couro malhado", null, null, 0, null, null);
        given(produtoService.buscarPorId(1L)).willReturn(produto);

        mockMvc.perform(get("/api/v1/public/produtos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.disponivel").value(false));
    }

    @Test
    void catalogoPublicoComEstoqueNuloMarcaComoIndisponivel() throws Exception {
        ProdutoDTO produto = new ProdutoDTO(1L, "Couro malhado", null, null, null, null, null);
        given(produtoService.buscarPorId(1L)).willReturn(produto);

        mockMvc.perform(get("/api/v1/public/produtos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.disponivel").value(false));
    }

    @Test
    void endpointProtegidoContinuaExigindoApiKeyAposAdicionarCatalogoPublico() throws Exception {
        mockMvc.perform(get("/api/v1/produtos"))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/estoque/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void verboHttpNaoSuportadoRetorna405ComFormatoApiError() throws Exception {
        mockMvc.perform(post("/api/v1/produtos").header(HEADER, SAPATOS_KEY))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void corsPreflightNoCatalogoPublicoLiberaOrigemDoFrontend() throws Exception {
        mockMvc.perform(options("/api/v1/public/produtos")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
            .andExpect(header().string("Access-Control-Allow-Methods", "GET"));
    }

    @Test
    void corsNaoELiberadoParaEndpointsProtegidos() throws Exception {
        mockMvc.perform(options("/api/v1/produtos")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void rotasAntigasSemV1NaoExistemMaisRetornam404() throws Exception {
        mockMvc.perform(get("/api/produtos").header(HEADER, SAPATOS_KEY))
            .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/estoque/1").header(HEADER, SAPATOS_KEY))
            .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/public/produtos"))
            .andExpect(status().isNotFound());
    }
}
