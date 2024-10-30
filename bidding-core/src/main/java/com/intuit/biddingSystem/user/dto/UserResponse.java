package com.intuit.biddingSystem.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intuit.biddingSystem.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {


    @JsonIgnore
    private UUID userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor to map from User entity
    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

}