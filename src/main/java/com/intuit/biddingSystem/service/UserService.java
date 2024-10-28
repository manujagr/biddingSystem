package com.intuit.biddingSystem.service;

import com.intuit.biddingSystem.dto.UserRegistrationRequest;
import com.intuit.biddingSystem.dto.UserResponse;
import com.intuit.biddingSystem.model.User;
import com.intuit.biddingSystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Use PasswordEncoder interface


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerUser(UserRegistrationRequest userRequest) {
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

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getUserId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setCreatedAt(LocalDateTime.now());
        userResponse.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getUserId());

        return userResponse;
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Retrieves a User by username.
     *
     * @param username The username of the user.
     * @return An Optional containing the User if found, else empty.
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves a User by email.
     *
     * @param email The email of the user.
     * @return An Optional containing the User if found, else empty.
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
