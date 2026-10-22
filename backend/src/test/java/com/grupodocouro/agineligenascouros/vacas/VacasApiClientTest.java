package com.grupodocouro.agineligenascouros.vacas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/**
 * Usa MockRestServiceServer (built-in do Spring Test, sem dependencia nova)
 * pra simular a API das Vacas - nao faz chamada real contra
 * 192.168.68.78, que pode nem estar alcancavel desta maquina.
 */
class VacasApiClientTest {

    private static final String BASE_URL = "http://vacas.exemplo.local:8000";
    private static final String TEST_API_KEY = "test-vacas-api-key";

    private MockRestServiceServer server;
    private VacasApiClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();

        VacasApiProperties properties = new VacasApiProperties();
        properties.setBaseUrl(BASE_URL);
        properties.setApiKey(TEST_API_KEY);

        client = new VacasApiClient(builder, properties);
    }

    @Test
    void listarRacasParseiaContratoDelasComQuantidadeDisponivelEPrecoString() {
        server.expect(requestTo(BASE_URL + "/api/parceiro/racas"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("X-API-Key", TEST_API_KEY))
            .andRespond(withSuccess(
                "[{\"id\":1,\"nome\":\"Angus\",\"quantidade_disponivel\":5,\"preco\":\"150.00\"},"
                    + "{\"id\":2,\"nome\":\"Nelore\",\"quantidade_disponivel\":0,\"preco\":\"80.50\"}]",
                MediaType.APPLICATION_JSON));

        List<RacaVacasDTO> racas = client.listarRacas();

        assertThat(racas).hasSize(2);
        assertThat(racas.get(0)).isEqualTo(new RacaVacasDTO(1L, "Angus", 5, "150.00"));
        assertThat(racas.get(1)).isEqualTo(new RacaVacasDTO(2L, "Nelore", 0, "80.50"));
    }

    @Test
    void buscarRacaRetorna200ComDados() {
        server.expect(requestTo(BASE_URL + "/api/parceiro/racas/1"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("X-API-Key", TEST_API_KEY))
            .andRespond(withSuccess(
                "{\"id\":1,\"nome\":\"Angus\",\"quantidade_disponivel\":5,\"preco\":\"150.00\"}",
                MediaType.APPLICATION_JSON));

        Optional<RacaVacasDTO> raca = client.buscarRaca(1L);

        assertThat(raca).contains(new RacaVacasDTO(1L, "Angus", 5, "150.00"));
    }

    @Test
    void buscarRacaRetorna404ComFormatoDeErroDelasVazioNaoLancaExcecao() {
        server.expect(requestTo(BASE_URL + "/api/parceiro/racas/999"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"detail\":\"Raca nao encontrada ou nao esta a venda.\"}"));

        Optional<RacaVacasDTO> raca = client.buscarRaca(999L);

        assertThat(raca).isEmpty();
    }

    @Test
    void nuncaEnviaChaveNoCorpoOuQueryDaRequisicao() {
        server.expect(requestTo(BASE_URL + "/api/parceiro/racas"))
            .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        client.listarRacas();

        // MockRestServiceServer ja valida a URL exata via requestTo() acima -
        // se a chave fosse enviada como query param, a URL nao casaria e o
        // teste falharia. A chave so vai no header (verificado nos outros testes).
        server.verify();
    }
}
