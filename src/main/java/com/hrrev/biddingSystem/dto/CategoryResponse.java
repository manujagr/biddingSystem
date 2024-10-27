package com.hrrev.biddingSystem.dto;

import com.hrrev.biddingSystem.model.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CategoryResponse {

    private UUID categoryId;
    private String name;
    private String description;

    public CategoryResponse(Category category) {
        this.categoryId = category.getCategoryId();
        this.name = category.getName();
        this.description = category.getDescription();
    }

    public CategoryResponse(UUID categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }
}

