package com.hrrev.biddingSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import javax.management.relation.Role;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
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

