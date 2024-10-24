package com.hrrev.biddingSystem.model;


import jakarta.persistence.*;

import javax.management.relation.Role;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String name;
    private String description;
    private BigDecimal basePrice;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
}
