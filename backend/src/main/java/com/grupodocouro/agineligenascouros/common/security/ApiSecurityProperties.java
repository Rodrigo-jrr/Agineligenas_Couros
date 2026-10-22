package com.grupodocouro.agineligenascouros.common.security;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Registro dos clientes autorizados a consumir /api/v1/** via API Key.
 *
 * <p>Cada cliente (ex.: Sapatos) tem uma entrada aqui com nome, chave (lida de
 * variavel de ambiente - nunca hardcoded) e as authorities que controlam quais
 * recursos ele pode acessar. Para registrar um novo cliente (ex.: Vacas, no
 * futuro) basta adicionar uma nova entrada em {@code app.security.clients} no
 * application.yml - nenhuma classe precisa ser alterada.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.security")
public class ApiSecurityProperties {

    private List<ClientProperties> clients = new ArrayList<>();

    @Getter
    @Setter
    public static class ClientProperties {
        private String name;
        private String key;
        private List<String> authorities = new ArrayList<>();
    }
}
