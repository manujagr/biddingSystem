package com.hrrev.biddingSystem.controller;

import com.hrrev.biddingSystem.dto.ProductRegistrationRequest;
import com.hrrev.biddingSystem.dto.ProductResponse;
import com.hrrev.biddingSystem.model.Authentication;
import com.hrrev.biddingSystem.model.Product;
import com.hrrev.biddingSystem.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param authentication The authentication information of the vendor.
     * @return ResponseEntity containing the created ProductResponse.
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRegistrationRequest productRequest,
                                           Authentication authentication) {
        try {
            UUID userId = getVendorIdFromAuth(authentication);
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

    /**
     * Helper method to extract vendor ID from Authentication.
     *
     * @param authentication The authentication information.
     * @return UUID representing the vendor's user ID.
     */
    private UUID getVendorIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getUserId() == null) {
            logger.error("Unauthorized access attempt detected: missing authentication details.");
            throw new NoSuchElementException("User is not authenticated.");
        }
        UUID userId = authentication.getUserId();
        logger.debug("Extracted vendor ID from authentication: {}", userId);
        return userId;
    }
}