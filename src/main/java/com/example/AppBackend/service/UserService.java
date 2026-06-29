package com.example.AppBackend.service;

import com.example.AppBackend.dto.CreateUserRequest;
import com.example.AppBackend.entity.User;
import com.example.AppBackend.exception.ResourceConflictException;
import com.example.AppBackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User provisionUser(CreateUserRequest request) {
        if (userRepository.existsById(request.userId())) {
            throw new ResourceConflictException("User already exists with id: " + request.userId());
        }

        User user = new User(request.userId(), null, request.role());
        return userRepository.save(user);
    }
}
