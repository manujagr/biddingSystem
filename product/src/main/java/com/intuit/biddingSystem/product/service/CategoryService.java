package com.intuit.biddingSystem.product.service;

import com.intuit.biddingSystem.product.dto.CategoryRequest;
import com.intuit.biddingSystem.product.dto.CategoryResponse;
import com.intuit.biddingSystem.product.model.Category;
import com.intuit.biddingSystem.product.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    public Optional<CategoryResponse> getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .map(this::mapToResponse);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse updateCategory(UUID categoryId, CategoryRequest categoryRequest) {
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    category.setName(categoryRequest.getName());
                    category.setDescription(categoryRequest.getDescription());
                    return categoryRepository.save(category);
                })
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public void deleteCategory(UUID categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(category.getCategoryId(), category.getName(), category.getDescription());
    }
}
