package com.example.AppBackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private final AuthProperties authProperties;

    public OpenApiConfig(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Bean
    public OpenAPI openAPI() {
        String authApiUrl = authProperties.getApi().getBaseUrl();
        final String securitySchemeName = "bearer-jwt";

        return new OpenAPI()
                .info(new Info()
                        .title("Test Submission API")
                        .description("""
                                REST API for fetching test templates and submitting completed test sessions.

                                **Authentication:** Obtain a short-lived access token from the Auth API at %s \
                                (login / refresh). Send it as `Authorization: Bearer <access_token>`. \
                                Tokens are RS256 JWTs; this service verifies them locally using the Auth API public key. \
                                The JWT `sub` claim must match a provisioned local user UUID.
                                """.formatted(authApiUrl))
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AppBackend")
                                .email("dev@example.com")))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Access token from Auth API (sub=userId, exp=expiration)")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
