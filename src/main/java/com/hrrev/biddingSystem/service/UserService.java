package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.dto.UserRegistrationRequest;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.repository.UserRepository;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegistrationRequest userRequest) {

        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        // Hash the password (Implement password hashing)
        user.setPasswordHash(hashPassword(userRequest.getPassword()));

        return userRepository.save(user);
    }

    private String hashPassword(String password) {
        // Implement password hashing logic (e.g., BCrypt)
        return password; // Placeholder
    }

}
