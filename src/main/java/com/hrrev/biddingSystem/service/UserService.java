package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.dto.UserRegistrationRequest;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationRequest userRequest) {
        logger.info("Registering user with username: {}", userRequest.getUsername());

        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            logger.error("Username {} already exists", userRequest.getUsername());
            throw new DataIntegrityViolationException("Username already exists");
        }

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            logger.error("Email {} already exists", userRequest.getEmail());
            throw new DataIntegrityViolationException("Email already exists");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPasswordHash(hashPassword(userRequest.getPassword()));

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getUserId());

        return savedUser;
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}