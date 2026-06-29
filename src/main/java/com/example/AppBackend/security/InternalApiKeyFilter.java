package com.example.AppBackend.security;

import com.example.AppBackend.config.AuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class InternalApiKeyFilter extends OncePerRequestFilter {

    private final AuthProperties authProperties;

    public InternalApiKeyFilter(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String configuredKey = authProperties.getApi().getInternalApiKey();
        String providedKey = request.getHeader(InternalServiceAuthentication.HEADER_NAME);

        if (configuredKey == null || configuredKey.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Internal API key is not configured");
            return;
        }

        if (!configuredKey.equals(providedKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid internal API key");
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(new InternalServiceAuthentication());
        filterChain.doFilter(request, response);
    }
}
