package com.example.AppBackend.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserService {

    public UUID requireUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new AccessDeniedException("Authenticated user required");
        }

        return UUID.fromString(jwtAuthentication.getToken().getSubject());
    }

    public void requireMatchingUserId(String userId) {
        UUID authenticatedUserId = requireUserId();
        UUID requestedUserId;
        try {
            requestedUserId = UUID.fromString(userId);
        } catch (IllegalArgumentException ex) {
            throw new AccessDeniedException("Invalid userId format");
        }

        if (!authenticatedUserId.equals(requestedUserId)) {
            throw new AccessDeniedException("userId does not match authenticated user");
        }
    }
}
