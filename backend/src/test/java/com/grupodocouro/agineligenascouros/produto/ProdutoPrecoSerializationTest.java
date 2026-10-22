package com.grupodocouro.agineligenascouros.produto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Confirma explicitamente a REGRA 2 do contrato com os consumidores
 * (Sapatos/Vacas): precos sempre com 2 casas decimais fixas no JSON (ex.:
 * {@code 3500.00}, nunca {@code 3500} ou {@code 3500.0}), inclusive quando o
 * valor tem zero a direita (ex.: {@code 10.00}). Usa a mesma configuracao de
 * inclusao de propriedades da aplicacao real (default-property-inclusion:
 * non_null), replicada aqui para o teste nao depender de contexto Spring.
 */
class ProdutoPrecoSerializationTest {

    private final ObjectMapper objectMapper =
        new ObjectMapper().setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

    @Test
    void produtoDtoSerializaPrecoComDuasCasasDecimaisFixas() throws Exception {
        ProdutoDTO produto = new ProdutoDTO(
            1L, "Couro premium", "Descricao", new BigDecimal("3500.00"), 5, "m2", 10L);

        String json = objectMapper.writeValueAsString(produto);

        assertThat(json).isEqualTo(
            "{\"id\":1,\"nome\":\"Couro premium\",\"descricao\":\"Descricao\",\"preco\":3500.00,"
                + "\"quantidadeDisponivel\":5,\"unidadeMedida\":\"m2\",\"fornecedorId\":10}");
    }

    @Test
    void produtoDtoNaoPerdeZeroADireitaNoPreco() throws Exception {
        ProdutoDTO produto = new ProdutoDTO(2L, "Couro simples", null, new BigDecimal("10.00"), null, null, null);

        String json = objectMapper.writeValueAsString(produto);

        assertThat(json).contains("\"preco\":10.00");
        assertThat(json).doesNotContain("\"preco\":10,");
        assertThat(json).doesNotContain("\"preco\":10.0,");
        assertThat(json).doesNotContain("\"preco\":10}");
    }

    @Test
    void produtoPublicoDtoTambemSerializaPrecoComDuasCasasDecimaisFixas() throws Exception {
        ProdutoPublicoDTO produto =
            new ProdutoPublicoDTO(1L, "Couro premium", "Descricao", new BigDecimal("3500.00"), "m2", true);

        String json = objectMapper.writeValueAsString(produto);

        assertThat(json).isEqualTo(
            "{\"id\":1,\"nome\":\"Couro premium\",\"descricao\":\"Descricao\",\"preco\":3500.00,"
                + "\"unidadeMedida\":\"m2\",\"disponivel\":true}");
    }
}
