package com.grupodocouro.agineligenascouros.vacas;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracao do cliente HTTP da API das Vacas (Etapa 4 - nosso backend
 * como CONSUMIDOR da API deles, sentido inverso do
 * {@code app.security.clients} que protege a NOSSA API).
 *
 * <p>{@code VACAS_API_KEY} e distinta de {@code VACAS_CONSUMER_API_KEY}
 * (usada pelo cliente Vacas pra consumir a NOSSA API) - nomes diferentes de
 * proposito, pra nao confundir a direcao do fluxo.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.vacas")
public class VacasApiProperties {

    private String baseUrl;
    private String apiKey;
}
