package com.example.AppBackend.service;

import com.example.AppBackend.config.AuthProperties;
import com.example.AppBackend.dto.AuthRegisterResponse;
import com.example.AppBackend.dto.RegisterRequest;
import com.example.AppBackend.entity.Role;
import com.example.AppBackend.entity.User;
import com.example.AppBackend.exception.ResourceConflictException;
import com.example.AppBackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final AuthProperties authProperties;

    public RegistrationService(UserRepository userRepository,
                               RestTemplate restTemplate,
                               AuthProperties authProperties) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.authProperties = authProperties;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        AuthRegisterResponse authResponse = callAuthApi(request);
        UUID userId = UUID.fromString(authResponse.userId());
        Role role = Role.valueOf(authResponse.role().toUpperCase(Locale.ROOT));

        if (userRepository.existsById(userId)) {
            throw new ResourceConflictException("User already exists with id: " + userId);
        }

        User user = new User(userId, request.username(), role);
        return userRepository.save(user);
    }

    private AuthRegisterResponse callAuthApi(RegisterRequest request) {
       String url = authProperties.getApi().getBaseUrl() + "/register_poo";
    
        Map<String, String> authRequest = Map.of(
                "email", request.email(),
                "password", request.password()
        );

        try {
            return restTemplate.postForObject(url, authRequest, AuthRegisterResponse.class);
        } catch (HttpStatusCodeException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), "Auth API registration failed", ex);
        }
    }
}
