package com.intuit.biddingSystem.product.controller;

import com.intuit.biddingSystem.coreUtils.authorization.utils.SecurityUtil;
import com.intuit.biddingSystem.product.dto.ProductRegistrationRequest;
import com.intuit.biddingSystem.product.dto.ProductResponse;
import com.intuit.biddingSystem.product.model.Category;
import com.intuit.biddingSystem.product.model.Product;
import com.intuit.biddingSystem.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductController using Mockito and JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductRegistrationRequest validProductRequest;
    private Product mockProduct;
    private ProductResponse mockProductResponse;
    private UUID mockUserId;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        // Initialize a valid ProductRegistrationRequest
        mockCategory = new Category();
        mockCategory.setCategoryId(UUID.randomUUID());
        mockCategory.setName("Electronics");
        mockCategory.setDescription("Electronic devices and accessories");

        // Initialize a valid ProductRegistrationRequest
        validProductRequest = new ProductRegistrationRequest();
        validProductRequest.setCategoryId(mockCategory.getCategoryId());
        validProductRequest.setName("Sample Product");
        validProductRequest.setDescription("Sample Description");
        validProductRequest.setBasePrice(BigDecimal.valueOf(19.99));
        validProductRequest.setImageUrl("http://example.com/image.jpg");

        // Initialize a mock Product and associate the Category with it
        mockProduct = new Product();
        mockProduct.setProductId(UUID.randomUUID());
        mockProduct.setName("Sample Product");
        mockProduct.setDescription("Sample Description");
        mockProduct.setBasePrice(BigDecimal.valueOf(19.99));
        mockProduct.setImageUrl("http://example.com/image.jpg");
        mockProduct.setCategory(mockCategory);  // Set the category

        // Initialize a mock ProductResponse based on the mock Product
        mockProductResponse = new ProductResponse(mockProduct);

        // Initialize a mock user UUID
        mockUserId = UUID.randomUUID();
    }

    /**
     * Test successful product creation with a valid product request.
     * Expects HTTP 201 Created and the ProductResponse in the body.
     */
    @Test
    @DisplayName("Create Product - Success")
    void createProduct_Success() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(mockUserId);

            when(productService.createProduct(validProductRequest, mockUserId)).thenReturn(mockProduct);

            ResponseEntity<?> responseEntity = productController.createProduct(validProductRequest);

            // Verify HTTP status
            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

            // Verify each field individually
            ProductResponse actualResponse = (ProductResponse) responseEntity.getBody();
            assertNotNull(actualResponse);
            assertEquals(mockProductResponse.getProductId(), actualResponse.getProductId());
            assertEquals(mockProductResponse.getName(), actualResponse.getName());
            assertEquals(mockProductResponse.getDescription(), actualResponse.getDescription());
            assertEquals(mockProductResponse.getBasePrice(), actualResponse.getBasePrice());
            assertEquals(mockProductResponse.getImageUrl(), actualResponse.getImageUrl());
            assertEquals(mockProductResponse.getCategory().getCategoryId(), actualResponse.getCategory().getCategoryId());
            assertEquals(mockProductResponse.getCategory().getName(), actualResponse.getCategory().getName());
            assertEquals(mockProductResponse.getCategory().getDescription(), actualResponse.getCategory().getDescription());
            assertEquals(mockProductResponse.getCreatedAt(), actualResponse.getCreatedAt());
            assertEquals(mockProductResponse.getUpdatedAt(), actualResponse.getUpdatedAt());

            // Verify that SecurityUtil and productService were called as expected
            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserUUID, times(1));
            verify(productService, times(1)).createProduct(validProductRequest, mockUserId);
        }
    }


    /**
     * Test product creation with a null ProductRegistrationRequest.
     * Expects HTTP 400 Bad Request and an error message.
     */
    @Test
    @DisplayName("Create Product - Null Request")
    void createProduct_NullRequest() {
        ResponseEntity<?> responseEntity = productController.createProduct(null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        verify(productService, never()).createProduct(any(), any());
    }

    /**
     * Test product creation when vendor is not found.
     * Expects HTTP 404 Not Found and an error message.
     */
    @Test
    @DisplayName("Create Product - Vendor Not Found")
    void createProduct_VendorNotFound() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(mockUserId);

            when(productService.createProduct(validProductRequest, mockUserId))
                    .thenThrow(new NoSuchElementException("Vendor not found"));

            ResponseEntity<?> responseEntity = productController.createProduct(validProductRequest);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertEquals("Vendor not found", responseEntity.getBody());

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserUUID, times(1));
            verify(productService, times(1)).createProduct(validProductRequest, mockUserId);
        }
    }

    /**
     * Test product creation with invalid product data.
     * Expects HTTP 400 Bad Request and an error message.
     */
    @Test
    @DisplayName("Create Product - Invalid Request")
    void createProduct_InvalidRequest() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(mockUserId);

            when(productService.createProduct(validProductRequest, mockUserId))
                    .thenThrow(new IllegalArgumentException("Invalid product request"));

            ResponseEntity<?> responseEntity = productController.createProduct(validProductRequest);

            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            assertEquals("Invalid product request", responseEntity.getBody());

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserUUID, times(1));
            verify(productService, times(1)).createProduct(validProductRequest, mockUserId);
        }
    }

    /**
     * Test product creation when an unexpected error occurs.
     * Expects HTTP 500 Internal Server Error and an error message.
     */
    @Test
    @DisplayName("Create Product - Internal Server Error")
    void createProduct_InternalServerError() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(mockUserId);

            when(productService.createProduct(validProductRequest, mockUserId))
                    .thenThrow(new RuntimeException("Unexpected error"));

            ResponseEntity<?> responseEntity = productController.createProduct(validProductRequest);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
            assertEquals("An unexpected error occurred.", responseEntity.getBody());

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserUUID, times(1));
            verify(productService, times(1)).createProduct(validProductRequest, mockUserId);
        }
    }

    /**
     * Test retrieving all products successfully.
     * Expects HTTP 200 OK and a list of ProductResponse in the body.
     */
    @Test
    @DisplayName("Get All Products - Success")
    void getAllProducts_Success() {
        List<ProductResponse> productList = new ArrayList<>();
        productList.add(mockProductResponse);

        when(productService.getAllProducts()).thenReturn(productList);

        ResponseEntity<?> responseEntity = productController.getAllProducts();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(productList, responseEntity.getBody());

        verify(productService, times(1)).getAllProducts();
    }

    /**
     * Test retrieving all products when no products are available.
     * Expects HTTP 200 OK and an empty list.
     */
    @Test
    @DisplayName("Get All Products - Empty List")
    void getAllProducts_EmptyList() {
        List<ProductResponse> emptyProductList = new ArrayList<>();

        when(productService.getAllProducts()).thenReturn(emptyProductList);

        ResponseEntity<?> responseEntity = productController.getAllProducts();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(emptyProductList, responseEntity.getBody());

        verify(productService, times(1)).getAllProducts();
    }
}
