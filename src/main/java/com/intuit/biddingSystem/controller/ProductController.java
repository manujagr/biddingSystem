package com.intuit.biddingSystem.controller;

import com.intuit.biddingSystem.dto.ProductRegistrationRequest;
import com.intuit.biddingSystem.dto.ProductResponse;
import com.intuit.biddingSystem.model.Product;
import com.intuit.biddingSystem.service.ProductService;
import com.intuit.biddingSystem.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        if (productRequest == null) {
            logger.error("Product registration request is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product registration request cannot be null");
        }
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