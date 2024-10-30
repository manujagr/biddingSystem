package com.intuit.biddingSystem.product.repository;


import com.intuit.biddingSystem.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);
    //Optional<Category> findById(UUID userId);
}

