package com.hrrev.biddingSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "authentication",
        indexes = {
                @Index(name = "idx_auth_username", columnList = "username")
        }
)
public class Authentication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // Constructors
    public Authentication() {}

    public Authentication(String username, String password) {
        this.username = username;
        this.password = password;
    }
}