package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.dto.ProductRegistrationRequest;
import com.hrrev.biddingSystem.model.Category;
import com.hrrev.biddingSystem.model.Product;
import com.hrrev.biddingSystem.model.Vendor;
import com.hrrev.biddingSystem.repository.CategoryRepository;
import com.hrrev.biddingSystem.repository.ProductRepository;
import com.hrrev.biddingSystem.repository.VendorRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

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




    public Product createProduct(ProductRegistrationRequest productRequest, Long vendorId) throws Exception {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new Exception("Vendor not found"));

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new Exception("Category not found"));

        Product product = new Product();
        product.setVendor(vendor);
        product.setCategory(category);
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setBasePrice(productRequest.getBasePrice());
        product.setImageUrl(productRequest.getImageUrl());

        // Implement product creation logic
        return productRepository.save(product);
    }

}

