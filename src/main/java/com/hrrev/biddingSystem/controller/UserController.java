package com.hrrev.biddingSystem.controller;

import com.hrrev.biddingSystem.dto.UserRegistrationRequest;
import com.hrrev.biddingSystem.dto.UserResponse;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user.
     *
     * @param userRegistrationRequest The user registration details.
     * @return ResponseEntity containing the created user or error message.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        if (userRegistrationRequest == null) {
            logger.error("User registration request is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration request cannot be null");
        }

        try {
            UserResponse createdUser = userService.registerUser(userRegistrationRequest);
            if (createdUser == null) {
                logger.error("UserService returned null for user registration");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user");
            }
            logger.info("User registered successfully with ID: {}", createdUser.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid user registration details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid registration details");

        } catch (NoSuchElementException e) {
            logger.error("Required resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Required resource not found");

        } catch (Exception e) {
            logger.error("An unexpected error occurred during registration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}