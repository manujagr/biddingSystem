package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.dto.ProductRegistrationRequest;
import com.hrrev.biddingSystem.model.Category;
import com.hrrev.biddingSystem.model.Product;
import com.hrrev.biddingSystem.model.Vendor;
import com.hrrev.biddingSystem.repository.CategoryRepository;
import com.hrrev.biddingSystem.repository.ProductRepository;
import com.hrrev.biddingSystem.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          VendorRepository vendorRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.vendorRepository = vendorRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product createProduct(ProductRegistrationRequest productRequest, UUID userId) {
        logger.info("Creating product for vendor ID: {}", userId);

        Vendor vendor = vendorRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Vendor not found for ID: {}", userId);
                    return new NoSuchElementException("Vendor not found");
                });

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> {
                    logger.error("Category not found for ID: {}", productRequest.getCategoryId());
                    return new NoSuchElementException("Category not found");
                });

        Product product = new Product();
        product.setVendor(vendor);
        product.setCategory(category);
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setBasePrice(productRequest.getBasePrice());
        product.setImageUrl(productRequest.getImageUrl());

        Product savedProduct = productRepository.save(product);
        logger.info("Product created successfully with ID: {}", savedProduct.getProductId());

        return savedProduct;
    }
}