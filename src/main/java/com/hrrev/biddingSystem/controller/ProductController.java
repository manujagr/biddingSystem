package com.hrrev.biddingSystem.controller;


import com.hrrev.biddingSystem.dto.ProductRegistrationRequest;
import com.hrrev.biddingSystem.dto.ProductResponse;
import com.hrrev.biddingSystem.dto.VendorRegistrationRequest;
import com.hrrev.biddingSystem.model.Authentication;
import com.hrrev.biddingSystem.model.Product;
import com.hrrev.biddingSystem.model.Vendor;
import com.hrrev.biddingSystem.service.ProductService;
import com.hrrev.biddingSystem.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRegistrationRequest productRequest,
                                           Authentication authentication) {
        try {
            Long vendorId = getVendorIdFromAuth(authentication);
            Product createdProduct = productService.createProduct(productRequest, vendorId);
            ProductResponse productResponse = new ProductResponse(createdProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper method to extract vendor ID from Authentication
    private Long getVendorIdFromAuth(Authentication authentication) {
        // Implement logic to extract vendor ID from authentication
        return authentication.getUserId(); // Placeholder
    }
}

