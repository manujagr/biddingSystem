package com.hrrev.biddingSystem.Controller;

import com.hrrev.biddingSystem.controller.UserController;
import com.hrrev.biddingSystem.dto.UserRegistrationRequest;
import com.hrrev.biddingSystem.dto.UserResponse;
import com.hrrev.biddingSystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for UserController.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService; // Mocked dependency

    @InjectMocks
    private UserController userController; // Controller under test

    private UserRegistrationRequest validRequest; // A valid registration request
    private UserResponse mockUserResponse; // Mocked successful response

    @BeforeEach
    void setUp() {
        // Initialize a valid UserRegistrationRequest
        validRequest = new UserRegistrationRequest();
        validRequest.setUsername("john_doe");
        validRequest.setEmail("john.doe@example.com");
        validRequest.setPassword("SecureP@ssw0rd");

        // Initialize a mock UserResponse
        mockUserResponse = new UserResponse();
        mockUserResponse.setUserId(UUID.randomUUID());
        mockUserResponse.setUsername("john_doe");
        mockUserResponse.setEmail("john.doe@example.com");
    }

    /**
     * Test the successful registration of a user.
     * Expects HTTP 201 Created and the UserResponse in the body.
     */
    @Test
    @DisplayName("Register User - Success")
    void registerUser_Success() {
        // Arrange: Mock the userService to return a successful UserResponse
        when(userService.registerUser(validRequest)).thenReturn(mockUserResponse);

        // Act: Call the controller's registerUser method
        ResponseEntity<?> responseEntity = userController.registerUser(validRequest);

        // Assert: Verify the response status and body
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode(), "Expected HTTP status 201 CREATED");
        assertEquals(mockUserResponse, responseEntity.getBody(), "Expected response body to match the mock UserResponse");

        // Verify that userService.registerUser was called once with validRequest
        verify(userService, times(1)).registerUser(validRequest);
    }

    /**
     * Test registration with invalid user details.
     * Expects HTTP 400 Bad Request and an error message.
     */
    @Test
    @DisplayName("Register User - Invalid Details")
    void registerUser_InvalidDetails() {
        // Arrange: Mock the userService to throw IllegalArgumentException
        when(userService.registerUser(validRequest)).thenThrow(new IllegalArgumentException("Invalid registration details"));

        // Act: Call the controller's registerUser method
        ResponseEntity<?> responseEntity = userController.registerUser(validRequest);

        // Assert: Verify the response status and body
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Expected HTTP status 400 BAD REQUEST");
        assertEquals("Invalid registration details", responseEntity.getBody(), "Expected error message for invalid details");

        // Verify that userService.registerUser was called once with validRequest
        verify(userService, times(1)).registerUser(validRequest);
    }

    /**
     * Test registration when a required resource is not found.
     * Expects HTTP 404 Not Found and an error message.
     */
    @Test
    @DisplayName("Register User - Resource Not Found")
    void registerUser_ResourceNotFound() {
        // Arrange: Mock the userService to throw NoSuchElementException
        when(userService.registerUser(validRequest)).thenThrow(new NoSuchElementException("Resource not found"));

        // Act: Call the controller's registerUser method
        ResponseEntity<?> responseEntity = userController.registerUser(validRequest);

        // Assert: Verify the response status and body
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode(), "Expected HTTP status 404 NOT FOUND");
        assertEquals("Required resource not found", responseEntity.getBody(), "Expected error message for resource not found");

        // Verify that userService.registerUser was called once with validRequest
        verify(userService, times(1)).registerUser(validRequest);
    }

    /**
     * Test registration when an unexpected error occurs.
     * Expects HTTP 500 Internal Server Error and a generic error message.
     */
    @Test
    @DisplayName("Register User - Internal Server Error")
    void registerUser_InternalServerError() {
        // Arrange: Mock the userService to throw a generic Exception
        when(userService.registerUser(validRequest)).thenThrow(new RuntimeException("Unexpected error"));

        // Act: Call the controller's registerUser method
        ResponseEntity<?> responseEntity = userController.registerUser(validRequest);

        // Assert: Verify the response status and body
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode(), "Expected HTTP status 500 INTERNAL SERVER ERROR");
        assertEquals("An unexpected error occurred.", responseEntity.getBody(), "Expected generic error message");

        // Verify that userService.registerUser was called once with validRequest
        verify(userService, times(1)).registerUser(validRequest);
    }

    /**
     * Test registration with null UserRegistrationRequest.
     * Expects HTTP 400 Bad Request due to validation failure.
     * Note: Since @Valid is not enforced without MockMvc, this test assumes manual validation.
     */
    @Test
    @DisplayName("Register User - Null Request")
    void registerUser_NullRequest() {
        // Arrange: Prepare a null request
        UserRegistrationRequest nullRequest = null;

        // Act: Call the controller's registerUser method
        ResponseEntity<?> responseEntity = userController.registerUser(nullRequest);

        // Assert: Verify the response status and body
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Expected HTTP status 400 BAD REQUEST");
        assertEquals("Registration request cannot be null", responseEntity.getBody(), "Expected error message for null request");

        // Verify that userService.registerUser was never called
        verify(userService, never()).registerUser(any());
    }

    /**
     * Test registration with empty username.
     * Expects HTTP 400 Bad Request due to validation failure.
     * Note: Without MockMvc, validation annotations are not processed, so manual handling is needed.
     */
    @Test
    @DisplayName("Register User - Empty Username")
    void registerUser_EmptyUsername() {
        // Arrange: Create a request with empty username
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("");
        request.setEmail("john.doe@example.com");
        request.setPassword("SecureP@ssw0rd");

        // Act: Call the controller's registerUser method
        // Since @Valid is not processed, the controller will pass the request to the service
        // Assuming the service will handle empty username appropriately
        when(userService.registerUser(request)).thenThrow(new IllegalArgumentException("Username cannot be empty"));

        ResponseEntity<?> responseEntity = userController.registerUser(request);

        // Assert: Verify the response status and body
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Expected HTTP status 400 BAD REQUEST");
        assertEquals("Invalid registration details", responseEntity.getBody(), "Expected error message for invalid details");

        // Verify that userService.registerUser was called once with the request
        verify(userService, times(1)).registerUser(request);
    }

    /**
     * Test registration with invalid email format.
     * Expects HTTP 400 Bad Request due to validation failure.
     * Note: Without MockMvc, validation annotations are not processed, so manual handling is needed.
     */
    @Test
    @DisplayName("Register User - Invalid Email Format")
    void registerUser_InvalidEmailFormat() {
        // Arrange: Create a request with invalid email
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("john_doe");
        request.setEmail("invalid-email-format");
        request.setPassword("SecureP@ssw0rd");

        // Act: Call the controller's registerUser method
        // Assuming the service will validate the email and throw an exception
        when(userService.registerUser(request)).thenThrow(new IllegalArgumentException("Invalid email format"));

        ResponseEntity<?> responseEntity = userController.registerUser(request);

        // Assert: Verify the response status and body
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Expected HTTP status 400 BAD REQUEST");
        assertEquals("Invalid registration details", responseEntity.getBody(), "Expected error message for invalid details");

        // Verify that userService.registerUser was called once with the request
        verify(userService, times(1)).registerUser(request);
    }

    /**
     * Test that the logger logs the appropriate messages on successful registration.
     * Note: Verifying logs typically requires additional libraries like LogCaptor or custom appenders.
     * This test focuses on ensuring the method executes without errors.
     */
    @Test
    @DisplayName("Register User - Logging on Success")
    void registerUser_LoggingOnSuccess() {
        // Arrange: Mock the userService to return a successful UserResponse
        when(userService.registerUser(validRequest)).thenReturn(mockUserResponse);

        // Act: Call the controller's registerUser method
        ResponseEntity<?> responseEntity = userController.registerUser(validRequest);

        // Assert: Verify the response
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode(), "Expected HTTP status 201 CREATED");
        assertEquals(mockUserResponse, responseEntity.getBody(), "Expected response body to match the mock UserResponse");

        // Since verifying logs requires additional setup, we ensure that the method executes as expected
        verify(userService, times(1)).registerUser(validRequest);
    }

    /**
     * Test that the logger logs the appropriate messages when an IllegalArgumentException is thrown.
     * Note: Verifying logs typically requires additional libraries like LogCaptor or custom appenders.
     * This test focuses on ensuring the method handles the exception correctly.
     */
    @Test
    @DisplayName("Register User - Logging on Invalid Details")
    void registerUser_LoggingOnInvalidDetails() {
        // Arrange: Mock the userService to throw IllegalArgumentException
        when(userService.registerUser(validRequest)).thenThrow(new IllegalArgumentException("Invalid registration details"));

        // Act: Call the controller's registerUser method
        ResponseEntity<?> responseEntity = userController.registerUser(validRequest);

        // Assert: Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Expected HTTP status 400 BAD REQUEST");
        assertEquals("Invalid registration details", responseEntity.getBody(), "Expected error message for invalid details");

        // Verify that userService.registerUser was called once with validRequest
        verify(userService, times(1)).registerUser(validRequest);
    }

    /**
     * Test that the controller handles unexpected exceptions gracefully.
     */
    @Test
    @DisplayName("Register User - Handling Unexpected Exceptions")
    void registerUser_HandleUnexpectedExceptions() {
        // Arrange: Mock the userService to throw a RuntimeException
        when(userService.registerUser(validRequest)).thenThrow(new RuntimeException("Database connection failed"));

        // Act: Call the controller's registerUser method
        ResponseEntity<?> responseEntity = userController.registerUser(validRequest);

        // Assert: Verify the response status and body
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode(), "Expected HTTP status 500 INTERNAL SERVER ERROR");
        assertEquals("An unexpected error occurred.", responseEntity.getBody(), "Expected generic error message");

        // Verify that userService.registerUser was called once with validRequest
        verify(userService, times(1)).registerUser(validRequest);
    }
}
