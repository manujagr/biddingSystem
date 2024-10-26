package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.Category;
import com.hrrev.biddingSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);
    //Optional<Category> findById(UUID userId);
}

