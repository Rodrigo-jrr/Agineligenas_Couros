package com.grupodocouro.agineligenascouros.common.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Tres cadeias de filtros, avaliadas em ordem (a primeira cujo
 * {@code securityMatcher} casar com a requisicao e a unica usada):
 * <ul>
 *   <li>{@code /api/v1/public/**} - catalogo publico (landing page), sem
 *       autenticacao nenhuma - precisa vir ANTES da cadeia protegida abaixo
 *       porque senao cairia no padrao mais generico {@code /api/v1/**}. E a
 *       UNICA cadeia com CORS liberado (chamada pelo navegador da landing
 *       page).</li>
 *   <li>{@code /api/v1/**} (restante, ex. {@code /api/v1/produtos},
 *       {@code /api/v1/estoque}) - exige API Key valida (ver
 *       {@link ApiKeyAuthenticationFilter}). Contrato B2B
 *       backend-to-backend, nunca chamado por navegador - sem CORS.</li>
 *   <li>demais caminhos (Swagger UI, OpenAPI docs, etc.) - sem autenticacao,
 *       sem CORS.</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain publicApiSecurityFilterChain(
        HttpSecurity http, CorsConfigurationSource publicCorsConfigurationSource) throws Exception {
        http
            .securityMatcher("/api/v1/public/**")
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(publicCorsConfigurationSource))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * CORS restrito a {@code /api/v1/public/**} - so GET, sem credentials, a
     * partir da origem do frontend (landing page React/Vite) configurada via
     * {@code FRONTEND_ORIGIN} (default {@code http://localhost:5173}, porta
     * padrao do Vite em dev). Nao e aplicado as cadeias de {@code /api/v1/**}
     * protegido nem a catch-all - essas nunca sao chamadas direto do
     * navegador.
     */
    @Bean
    public CorsConfigurationSource publicCorsConfigurationSource(
        @Value("${app.cors.frontend-origin}") String frontendOrigin) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendOrigin));
        configuration.setAllowedMethods(List.of("GET"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/public/**", configuration);
        return source;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(
        HttpSecurity http,
        ApiSecurityProperties apiSecurityProperties,
        ApiAuthenticationEntryPoint authenticationEntryPoint,
        ApiAccessDeniedHandler accessDeniedHandler) throws Exception {

        http
            .securityMatcher("/api/v1/**")
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler))
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .addFilterBefore(
                new ApiKeyAuthenticationFilter(apiSecurityProperties), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
