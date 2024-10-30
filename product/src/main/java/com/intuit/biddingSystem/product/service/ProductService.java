package com.intuit.biddingSystem.product.service;

import com.intuit.biddingSystem.product.dto.ProductRegistrationRequest;
import com.intuit.biddingSystem.product.dto.ProductResponse;
import com.intuit.biddingSystem.product.model.Category;
import com.intuit.biddingSystem.product.model.Product;
import com.intuit.biddingSystem.product.repository.CategoryRepository;
import com.intuit.biddingSystem.product.repository.ProductRepository;
import com.intuit.biddingSystem.user.model.User;
import com.intuit.biddingSystem.user.repository.UserRepository;
import com.intuit.biddingSystem.vendor.model.Vendor;
import com.intuit.biddingSystem.vendor.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository,
                          VendorRepository vendorRepository,
                          CategoryRepository categoryRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.vendorRepository = vendorRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public Product createProduct(ProductRegistrationRequest productRequest, UUID userId) {
        logger.info("Creating product for vendor ID: {}", userId);

        User user = userRepository.findById(userId).get();
        Vendor vendor = vendorRepository.findByUser(user)
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

    public List<ProductResponse> getAllProducts() {
        List<Product> productsList = productRepository.findAll();
        return productsList.stream()
                .map(ProductResponse::new) // Use the ProductResponse constructor for mapping
                .collect(Collectors.toList());
    }
}