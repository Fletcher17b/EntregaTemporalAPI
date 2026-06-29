package com.example.AppBackend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class JwtDecoderConfig {

    @Bean
    @Profile("!test")
    JwtDecoder jwtDecoder(AuthProperties authProperties, ResourceLoader resourceLoader) throws Exception {
        RSAPublicKey publicKey = resolvePublicKey(authProperties, resourceLoader);
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    private RSAPublicKey resolvePublicKey(AuthProperties authProperties, ResourceLoader resourceLoader)
            throws Exception {
        String pem = authProperties.getJwt().getPublicKey();
        if (pem != null && !pem.isBlank()) {
            return parsePublicKey(pem);
        }

        Resource resource = resourceLoader.getResource(authProperties.getJwt().getPublicKeyLocation());
        if (!resource.exists()) {
            throw new IllegalStateException(
                    "JWT public key not configured. Set JWT_PUBLIC_KEY or place a PEM file at "
                            + authProperties.getJwt().getPublicKeyLocation()
                            + " (must match the Auth API private signing key).");
        }

        try {
            pem = resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read JWT public key from "
                    + authProperties.getJwt().getPublicKeyLocation(), ex);
        }
        return parsePublicKey(pem);
    }

    static RSAPublicKey parsePublicKey(String pem) throws Exception {
        String normalized = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(normalized);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
    }
}
