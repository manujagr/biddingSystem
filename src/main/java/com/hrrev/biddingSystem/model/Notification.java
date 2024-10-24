package com.hrrev.biddingSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import javax.management.relation.Role;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    private UUID id;
    private UUID userId;
    private String message;
    private LocalDateTime sentTime;

    // Getters and setters
}

