package com.grupodocouro.agineligenascouros.estoque;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grupodocouro.agineligenascouros.produto.Produto;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

/**
 * Confirma explicitamente a REGRA 4 do contrato com os consumidores: datas
 * em ISO 8601 completo, com offset de timezone (ex.:
 * {@code 2026-10-14T15:30:00-03:00}), nunca sem offset.
 */
class EstoqueDTOSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void atualizadoEmSerializaComOffsetDeTimezoneExplicito() throws Exception {
        OffsetDateTime atualizadoEm = OffsetDateTime.of(2026, 10, 14, 15, 30, 0, 0, ZoneOffset.of("-03:00"));
        EstoqueDTO estoqueDTO = new EstoqueDTO(1L, 5, atualizadoEm);

        String json = objectMapper.writeValueAsString(estoqueDTO);

        assertThat(json).contains("\"atualizadoEm\":\"2026-10-14T15:30:00-03:00\"");
    }

    @Test
    void fromConverteParaOffsetDeBrasiliaIndependenteDaZonaOrigem() {
        Produto produto = produtoComId(42L);
        // Meia-noite UTC = 21h do dia anterior em Brasilia (-03:00, sem horario de verao).
        OffsetDateTime meiaNoiteUtc = OffsetDateTime.of(2026, 10, 14, 0, 0, 0, 0, ZoneOffset.UTC);
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidadeDisponivel(3);
        estoque.setAtualizadoEm(meiaNoiteUtc);

        EstoqueDTO dto = EstoqueDTO.from(estoque);

        assertThat(dto.atualizadoEm().getOffset()).isEqualTo(ZoneOffset.of("-03:00"));
        assertThat(dto.atualizadoEm().toInstant()).isEqualTo(meiaNoiteUtc.toInstant());
        assertThat(dto.atualizadoEm().toLocalDateTime().toString()).isEqualTo("2026-10-13T21:00");
    }

    private static Produto produtoComId(Long id) {
        Produto produto = new Produto();
        produto.setId(id);
        return produto;
    }
}
