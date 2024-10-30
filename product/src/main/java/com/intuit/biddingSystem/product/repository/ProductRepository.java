package com.intuit.biddingSystem.product.repository;


import com.intuit.biddingSystem.product.model.Category;
import com.intuit.biddingSystem.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<com.intuit.biddingSystem.product.model.Product, UUID> {
    List<Product> findByCategory(Category category);
}
