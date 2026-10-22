package com.grupodocouro.agineligenascouros.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Autentica requisicoes para /api/v1/** via API Key enviada no header
 * {@value #API_KEY_HEADER}.
 *
 * <p>Regras (ver especificacao da Etapa 3):
 * <ul>
 *   <li>sem header -&gt; segue sem autenticar (Spring Security responde 401 mais
 *       adiante, via {@link ApiAuthenticationEntryPoint}).</li>
 *   <li>header presente mas sem correspondencia em nenhum cliente registrado
 *       -&gt; idem, 401.</li>
 *   <li>header valido -&gt; autentica com as authorities do cliente; a
 *       autorizacao por recurso (403 quando falta permissao) e feita depois,
 *       via {@code @PreAuthorize} nos controllers.</li>
 * </ul>
 *
 * <p>A comparacao da chave usa {@link MessageDigest#isEqual} (nunca
 * {@code String.equals}) para nao vazar, por timing, quantos caracteres da
 * chave enviada coincidem com a chave real. A chave nunca e logada.
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "X-API-Key";

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

    private final List<ApiSecurityProperties.ClientProperties> clients;

    public ApiKeyAuthenticationFilter(ApiSecurityProperties properties) {
        this.clients = properties.getClients();
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String providedKey = request.getHeader(API_KEY_HEADER);

        if (StringUtils.hasText(providedKey)) {
            findMatchingClient(providedKey).ifPresentOrElse(
                client -> authenticate(client, request),
                () -> log.warn("API Key invalida recebida em {} {}", request.getMethod(), request.getRequestURI()));
        }

        filterChain.doFilter(request, response);
    }

    private Optional<ApiSecurityProperties.ClientProperties> findMatchingClient(String providedKey) {
        byte[] providedBytes = providedKey.getBytes(StandardCharsets.UTF_8);
        ApiSecurityProperties.ClientProperties match = null;
        for (ApiSecurityProperties.ClientProperties client : clients) {
            if (!StringUtils.hasText(client.getKey())) {
                continue;
            }
            byte[] expectedBytes = client.getKey().getBytes(StandardCharsets.UTF_8);
            if (MessageDigest.isEqual(providedBytes, expectedBytes)) {
                match = client;
            }
        }
        return Optional.ofNullable(match);
    }

    private void authenticate(ApiSecurityProperties.ClientProperties client, HttpServletRequest request) {
        Set<SimpleGrantedAuthority> authorities = client.getAuthorities().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());

        ApiKeyAuthenticationToken authentication = new ApiKeyAuthenticationToken(client.getName(), authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug(
            "Cliente '{}' autenticado para {} {}", client.getName(), request.getMethod(), request.getRequestURI());
    }
}
