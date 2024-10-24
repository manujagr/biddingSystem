package com.hrrev.biddingSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "vendors")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID vendorId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String companyName;
    private String contactInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
}