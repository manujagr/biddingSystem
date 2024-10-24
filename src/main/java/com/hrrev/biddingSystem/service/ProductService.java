package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.model.Product;
import com.hrrev.biddingSystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        // Implement product creation logic
        return productRepository.save(product);
    }

    // Additional methods
}

