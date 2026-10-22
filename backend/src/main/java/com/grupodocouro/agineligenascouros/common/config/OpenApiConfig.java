package com.grupodocouro.agineligenascouros.common.config;

import com.grupodocouro.agineligenascouros.common.security.ApiKeyAuthenticationFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "ApiKeyAuth",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER,
    paramName = ApiKeyAuthenticationFilter.API_KEY_HEADER,
    description = "Chave de API do cliente, enviada no header " + ApiKeyAuthenticationFilter.API_KEY_HEADER)
public class OpenApiConfig {

    @Bean
    public OpenAPI agineligenasCourosOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Agineligenas Couros - API Produto/Estoque")
                .version("v1")
                .description("Consulta de produtos e estoque do Grupo do Couro. "
                    + "Todos os endpoints exigem API Key no header "
                    + ApiKeyAuthenticationFilter.API_KEY_HEADER + "."))
            .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"));
    }
}
