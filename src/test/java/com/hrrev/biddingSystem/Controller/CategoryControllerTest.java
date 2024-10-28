package com.hrrev.biddingSystem.Controller;

import com.hrrev.biddingSystem.controller.CategoryController;
import com.hrrev.biddingSystem.dto.CategoryRequest;
import com.hrrev.biddingSystem.dto.CategoryResponse;
import com.hrrev.biddingSystem.service.CategoryService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryController using Mockito and JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryRequest validCategoryRequest;
    private CategoryResponse mockCategoryResponse;
    private UUID mockCategoryId;
    private Validator validator;

    @BeforeEach
    void setUp() {
        // Initialize Validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Initialize a valid CategoryRequest
        validCategoryRequest = new CategoryRequest();
        validCategoryRequest.setName("Electronics");
        validCategoryRequest.setDescription("Electronic devices and accessories");

        // Initialize a mock CategoryResponse
        mockCategoryId = UUID.randomUUID();
        mockCategoryResponse = new CategoryResponse(mockCategoryId, "Electronics", "Electronic devices and accessories");
    }

    /**
     * Test creating a category with a valid request.
     * Expects HTTP 201 Created and the CategoryResponse in the body.
     */
    @Test
    @DisplayName("Create Category - Success")
    void createCategory_Success() {
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(mockCategoryResponse);

        ResponseEntity<?> responseEntity = categoryController.createCategory(validCategoryRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(mockCategoryResponse, responseEntity.getBody());

        verify(categoryService, times(1)).createCategory(any(CategoryRequest.class));
    }

    /**
     * Test creating a category with a null request.
     * Expects HTTP 400 Bad Request.
     */
    @Test
    @DisplayName("Create Category - Null Request")
    void createCategory_NullRequest() {
        ResponseEntity<?> responseEntity = categoryController.createCategory(null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Category registration request cannot be null", responseEntity.getBody());

        verify(categoryService, never()).createCategory(any(CategoryRequest.class));
    }

    /**
     * Test creating a category with an invalid request (empty name).
     * Expects HTTP 400 Bad Request due to validation failure.
     */
    @Test
    @DisplayName("Create Category - Invalid Request")
    void createCategory_InvalidRequest() {
        CategoryRequest invalidRequest = new CategoryRequest(); // Missing name

        // Manually validate the request
        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty(), "Validation should fail for invalid CategoryRequest");

        // Since we're bypassing actual Spring validation, directly check for BAD_REQUEST without calling the service
        ResponseEntity<?> responseEntity = categoryController.createCategory(invalidRequest);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        verify(categoryService, never()).createCategory(any(CategoryRequest.class));
    }

    /**
     * Test retrieving a category by ID when it exists.
     * Expects HTTP 200 OK and the CategoryResponse in the body.
     */
    @Test
    @DisplayName("Get Category By ID - Success")
    void getCategoryById_Success() {
        when(categoryService.getCategoryById(mockCategoryId)).thenReturn(Optional.of(mockCategoryResponse));

        ResponseEntity<CategoryResponse> responseEntity = categoryController.getCategoryById(mockCategoryId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(mockCategoryResponse, responseEntity.getBody());

        verify(categoryService, times(1)).getCategoryById(mockCategoryId);
    }

    /**
     * Test retrieving a category by ID when it does not exist.
     * Expects HTTP 404 Not Found.
     */
    @Test
    @DisplayName("Get Category By ID - Not Found")
    void getCategoryById_NotFound() {
        when(categoryService.getCategoryById(mockCategoryId)).thenReturn(Optional.empty());

        ResponseEntity<CategoryResponse> responseEntity = categoryController.getCategoryById(mockCategoryId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        verify(categoryService, times(1)).getCategoryById(mockCategoryId);
    }

    /**
     * Test retrieving all categories when categories are available.
     * Expects HTTP 200 OK and a list of CategoryResponse in the body.
     */
    @Test
    @DisplayName("Get All Categories - Success")
    void getAllCategories_Success() {
        List<CategoryResponse> categories = List.of(mockCategoryResponse);

        when(categoryService.getAllCategories()).thenReturn(categories);

        ResponseEntity<List<CategoryResponse>> responseEntity = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().size());
        assertEquals(categories, responseEntity.getBody());

        verify(categoryService, times(1)).getAllCategories();
    }

    /**
     * Test retrieving all categories when no categories are available.
     * Expects HTTP 200 OK and an empty list.
     */
    @Test
    @DisplayName("Get All Categories - Empty List")
    void getAllCategories_EmptyList() {
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        ResponseEntity<List<CategoryResponse>> responseEntity = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(0, responseEntity.getBody().size());

        verify(categoryService, times(1)).getAllCategories();
    }

    /**
     * Test updating a category with a valid request.
     * Expects HTTP 200 OK and the updated CategoryResponse in the body.
     */
    @Test
    @DisplayName("Update Category - Success")
    void updateCategory_Success() {
        when(categoryService.updateCategory(eq(mockCategoryId), any(CategoryRequest.class))).thenReturn(mockCategoryResponse);

        ResponseEntity<CategoryResponse> responseEntity = categoryController.updateCategory(mockCategoryId, validCategoryRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(mockCategoryResponse, responseEntity.getBody());

        verify(categoryService, times(1)).updateCategory(eq(mockCategoryId), any(CategoryRequest.class));
    }

    /**
     * Test updating a category when it does not exist.
     * Expects HTTP 404 Not Found.
     */
    @Test
    @DisplayName("Update Category - Not Found")
    void updateCategory_NotFound() {
        when(categoryService.updateCategory(eq(mockCategoryId), any(CategoryRequest.class)))
                .thenThrow(new IllegalArgumentException("Category not found"));

        ResponseEntity<CategoryResponse> responseEntity = categoryController.updateCategory(mockCategoryId, validCategoryRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        verify(categoryService, times(1)).updateCategory(eq(mockCategoryId), any(CategoryRequest.class));
    }

    /**
     * Test deleting a category by ID.
     * Expects HTTP 204 No Content.
     */
    @Test
    @DisplayName("Delete Category - Success")
    void deleteCategory_Success() {
        ResponseEntity<Void> responseEntity = categoryController.deleteCategory(mockCategoryId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        verify(categoryService, times(1)).deleteCategory(mockCategoryId);
    }
}
