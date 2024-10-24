package com.hrrev.biddingSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
}