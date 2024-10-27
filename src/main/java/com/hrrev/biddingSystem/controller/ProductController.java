package com.hrrev.biddingSystem.controller;

import com.hrrev.biddingSystem.dto.ProductRegistrationRequest;
import com.hrrev.biddingSystem.dto.ProductResponse;
import com.hrrev.biddingSystem.model.Product;
import com.hrrev.biddingSystem.service.ProductService;
import com.hrrev.biddingSystem.util.CustomUserDetails;
import com.hrrev.biddingSystem.util.SecurityUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Endpoint to create a new product associated with a vendor.
     *
     * @param productRequest The product registration details.
     * @return ResponseEntity containing the created ProductResponse.
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRegistrationRequest productRequest) {
        UUID userUUID = null;
        try {

            UUID userId = SecurityUtil.getCurrentUserUUID();
            Product createdProduct = productService.createProduct(productRequest, userId);
            ProductResponse productResponse = new ProductResponse(createdProduct);
            logger.info("Product created successfully for vendor ID: {}", userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);

        } catch (NoSuchElementException e) {
            logger.error("Vendor ID not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");

        } catch (IllegalArgumentException e) {
            logger.error("Invalid product request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid product request");

        } catch (Exception e) {
            logger.error("An unexpected error occurred while creating the product: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(){
        List<ProductResponse> productResponse = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }
}