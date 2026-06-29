package com.example.AppBackend.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class InternalServiceAuthentication extends AbstractAuthenticationToken {

    public static final String HEADER_NAME = "X-Internal-Api-Key";

    public InternalServiceAuthentication() {
        super(AuthorityUtils.createAuthorityList("ROLE_INTERNAL"));
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return "auth-api";
    }
}
