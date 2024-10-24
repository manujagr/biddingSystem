package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.Category;
import com.hrrev.biddingSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}

