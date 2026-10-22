package com.grupodocouro.agineligenascouros.vacas;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP da API das Vacas (Etapa 4 - nosso backend como CONSUMIDOR).
 * Chama os 2 endpoints do contrato delas: listagem e busca por id.
 *
 * <p>A chave ({@code VACAS_API_KEY}) nunca e logada - mesmo padrao do nosso
 * {@code ApiKeyAuthenticationFilter} pro lado de entrada.
 */
@Component
public class VacasApiClient {

    private static final String RACAS_PATH = "/api/parceiro/racas";
    private static final String HEADER_API_KEY = "X-API-Key";

    private final RestClient restClient;

    public VacasApiClient(RestClient.Builder restClientBuilder, VacasApiProperties properties) {
        this.restClient = restClientBuilder
            .baseUrl(properties.getBaseUrl())
            .defaultHeader(HEADER_API_KEY, properties.getApiKey())
            .build();
    }

    /** GET /api/parceiro/racas - lista completa, em ordem alfabetica (contrato delas). */
    public List<RacaVacasDTO> listarRacas() {
        List<RacaVacasDTO> racas = restClient.get()
            .uri(RACAS_PATH)
            .retrieve()
            .body(new ParameterizedTypeReference<List<RacaVacasDTO>>() { });
        return racas != null ? racas : List.of();
    }

    /** GET /api/parceiro/racas/{id} - vazio se 404 (nao existe ou nao esta a venda). */
    public Optional<RacaVacasDTO> buscarRaca(Long id) {
        try {
            RacaVacasDTO raca = restClient.get()
                .uri(RACAS_PATH + "/{id}", id)
                .retrieve()
                .body(RacaVacasDTO.class);
            return Optional.ofNullable(raca);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }

    /**
     * Extrai a mensagem do formato de erro das Vacas ({@code {"detail": "..."}},
     * diferente do nosso {@code ApiError}) pra enriquecer logs. Usado pelo
     * service de sincronizacao quando uma chamada individual falha.
     */
    public static String extractDetail(HttpClientErrorException ex) {
        try {
            VacasErrorResponse error = ex.getResponseBodyAs(VacasErrorResponse.class);
            return error != null ? error.detail() : ex.getStatusText();
        } catch (Exception parseError) {
            return ex.getStatusText();
        }
    }
}
