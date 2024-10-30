package com.intuit.biddingSystem.coreUtils.authorization.service;

import com.intuit.biddingSystem.coreUtils.authorization.model.CustomUserDetails;
import com.intuit.biddingSystem.user.model.User;
import com.intuit.biddingSystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final com.intuit.biddingSystem.user.repository.UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Fetch user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Return a UserDetails object
        return new CustomUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getPasswordHash(),
                Collections.emptyList() // Add authorities if applicable
        );
    }
}
