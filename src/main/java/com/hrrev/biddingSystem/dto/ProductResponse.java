package com.hrrev.biddingSystem.dto;

import com.hrrev.biddingSystem.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ProductResponse {

    private UUID productId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String imageUrl;
    private CategoryResponse category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.basePrice = product.getBasePrice();
        this.imageUrl = product.getImageUrl();
        this.category = new CategoryResponse(product.getCategory());
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }

    // Getters and Setters
}

