package com.intuit.biddingSystem.Controller;

import com.intuit.biddingSystem.controller.VendorController;
import com.intuit.biddingSystem.dto.VendorRegistrationRequest;
import com.intuit.biddingSystem.dto.VendorResponse;
import com.intuit.biddingSystem.model.User;
import com.intuit.biddingSystem.model.Vendor;
import com.intuit.biddingSystem.service.VendorService;
import com.intuit.biddingSystem.util.SecurityUtil;
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

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VendorController using Mockito and JUnit 5.
 */
@ExtendWith(MockitoExtension.class)
class VendorControllerTest {

    @Mock
    private VendorService vendorService;

    @InjectMocks
    private VendorController vendorController;

    private VendorRegistrationRequest validRequest;
    private Vendor mockVendor;
    private VendorResponse mockVendorResponse;
    private UUID mockUserId;

    @BeforeEach
    void setUp() {
        // Initialize a User object to associate with Vendor
        User mockUser = new User();
        mockUser.setUserId(UUID.randomUUID());
        mockUser.setUsername("john_doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPasswordHash("hashed_password");

        // Initialize a Vendor and associate the User with it
        mockVendor = new Vendor();
        mockVendor.setVendorId(UUID.randomUUID());
        mockVendor.setUser(mockUser);
        mockVendor.setCompanyName("Acme Corp");
        mockVendor.setContactInfo("contact@acme.com");

        // Initialize VendorResponse with the mock Vendor
        mockVendorResponse = new VendorResponse(mockVendor);

        // Initialize a mock VendorRegistrationRequest
        validRequest = new VendorRegistrationRequest();
        validRequest.setCompanyName("Acme Corp");
        validRequest.setContactInfo("contact@acme.com");

        // Initialize a mock user UUID
        mockUserId = mockUser.getUserId();
    }

    @Test
    @DisplayName("Register Vendor - Success")
    void registerVendor_Success() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(mockUserId);

            when(vendorService.registerVendor(validRequest, mockUserId)).thenReturn(mockVendor);

            ResponseEntity<?> responseEntity = vendorController.registerVendor(validRequest);

            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            VendorResponse actualResponse = (VendorResponse) responseEntity.getBody();

            // Field-by-field comparison
            assertEquals(mockVendorResponse.getCompanyName(), actualResponse.getCompanyName());
            assertEquals(mockVendorResponse.getContactInfo(), actualResponse.getContactInfo());
            assertEquals(mockVendorResponse.getUserId(), actualResponse.getUserId());
            assertEquals(mockVendorResponse.getUsername(), actualResponse.getUsername());
            assertEquals(mockVendorResponse.getEmail(), actualResponse.getEmail());
            assertEquals(mockVendorResponse.getCreatedAt(), actualResponse.getCreatedAt());
            assertEquals(mockVendorResponse.getUpdatedAt(), actualResponse.getUpdatedAt());

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserUUID, times(1));
            verify(vendorService, times(1)).registerVendor(validRequest, mockUserId);
        }
    }

    @Test
    @DisplayName("Register Vendor - Null Request")
    void registerVendor_NullRequest() {
        ResponseEntity<?> responseEntity = vendorController.registerVendor(null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Vendor registration request cannot be null", responseEntity.getBody());

        verify(vendorService, never()).registerVendor(any(), any());
    }

    @Test
    @DisplayName("Register Vendor - Invalid Details")
    void registerVendor_InvalidDetails() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(mockUserId);

            when(vendorService.registerVendor(validRequest, mockUserId))
                    .thenThrow(new IllegalArgumentException("Invalid vendor registration details"));

            ResponseEntity<?> responseEntity = vendorController.registerVendor(validRequest);

            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            assertEquals("Invalid vendor registration details", responseEntity.getBody());

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserUUID, times(1));
            verify(vendorService, times(1)).registerVendor(validRequest, mockUserId);
        }
    }

    @Test
    @DisplayName("Register Vendor - Resource Not Found")
    void registerVendor_ResourceNotFound() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(mockUserId);

            when(vendorService.registerVendor(validRequest, mockUserId))
                    .thenThrow(new NoSuchElementException("Required resource not found"));

            ResponseEntity<?> responseEntity = vendorController.registerVendor(validRequest);

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
            assertEquals("Required resource not found", responseEntity.getBody());

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserUUID, times(1));
            verify(vendorService, times(1)).registerVendor(validRequest, mockUserId);
        }
    }

    @Test
    @DisplayName("Register Vendor - Internal Server Error")
    void registerVendor_InternalServerError() {
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(mockUserId);

            when(vendorService.registerVendor(validRequest, mockUserId))
                    .thenThrow(new RuntimeException("Database connection failed"));

            ResponseEntity<?> responseEntity = vendorController.registerVendor(validRequest);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
            assertEquals("An unexpected error occurred.", responseEntity.getBody());

            mockedSecurityUtil.verify(SecurityUtil::getCurrentUserUUID, times(1));
            verify(vendorService, times(1)).registerVendor(validRequest, mockUserId);
        }
    }
}
