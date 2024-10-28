package com.intuit.biddingSystem.repository;

import com.intuit.biddingSystem.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);
    //Optional<Category> findById(UUID userId);
}

