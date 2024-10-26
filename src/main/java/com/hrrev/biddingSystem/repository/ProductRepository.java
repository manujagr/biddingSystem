package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.Category;
import com.hrrev.biddingSystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByCategory(Category category);
}
