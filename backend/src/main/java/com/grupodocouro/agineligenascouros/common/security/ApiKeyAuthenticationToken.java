package com.grupodocouro.agineligenascouros.common.security;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Authentication representando um cliente de API identificado por API Key.
 * Nunca guarda a chave em si (nem como credentials) para evitar que ela
 * acabe exposta em logs ou em algum toString/debug acidental.
 */
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String clientName;

    public ApiKeyAuthenticationToken(String clientName, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.clientName = clientName;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return clientName;
    }
}
