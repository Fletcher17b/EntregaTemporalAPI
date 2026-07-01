package com.example.AppBackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private final Api api = new Api();
    private final Jwt jwt = new Jwt();

    public Api getApi() {
        return api;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public static class Api {

        /**
         * Base URL of the Auth API (login, refresh, logout). Default port 8081.
         */
        private String baseUrl = "http://localhost:8081";

        /**
         * Auth API registration endpoint.
         */
        private String registerEndpoint = "/register_poo";

        /**
         * Shared secret for service-to-service calls from Auth API.
         */
        private String internalApiKey = "";

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getRegisterEndpoint() {
            return registerEndpoint;
        }

        public void setRegisterEndpoint(String registerEndpoint) {
            this.registerEndpoint = registerEndpoint;
        }

        public String getInternalApiKey() {
            return internalApiKey;
        }

        public void setInternalApiKey(String internalApiKey) {
            this.internalApiKey = internalApiKey;
        }
    }

    public static class Jwt {

        /**
         * PEM-encoded RSA public key (optional if public-key-location is set).
         */
        private String publicKey = "";

        private String publicKeyLocation = "classpath:jwt-public.pem";

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPublicKeyLocation() {
            return publicKeyLocation;
        }

        public void setPublicKeyLocation(String publicKeyLocation) {
            this.publicKeyLocation = publicKeyLocation;
        }
    }
}
