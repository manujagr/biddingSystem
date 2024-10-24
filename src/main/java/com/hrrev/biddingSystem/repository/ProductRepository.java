package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.Category;
import com.hrrev.biddingSystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
}
