package com.example.AppBackend;

import com.example.AppBackend.config.AuthProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties(AuthProperties.class)
public class AppBackendApplication {

    private static final Logger log = LoggerFactory.getLogger(AppBackendApplication.class);

    // This injects the value from auth.api.base-url in your application.properties
    @Value("${auth.api.base-url}") 
    private String authApiBaseUrl;

    public static void main(String[] args) {
        SpringApplication.run(AppBackendApplication.class, args);
    }

    @PostConstruct
    public void logAuthUrl() {
        log.error("AUTH_API_URL resolved to = [{}]", authApiBaseUrl); 
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}