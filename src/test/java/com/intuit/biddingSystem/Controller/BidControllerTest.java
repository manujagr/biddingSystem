package com.intuit.biddingSystem.Controller;

import com.intuit.biddingSystem.controller.BidController;
import com.intuit.biddingSystem.dto.BidRegistrationRequest;
import com.intuit.biddingSystem.model.Bid;
import com.intuit.biddingSystem.service.BidService;
import com.intuit.biddingSystem.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BidControllerTest {

    @Mock
    private BidService bidService;

    @InjectMocks
    private BidController bidController;

    private LocalValidatorFactoryBean validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
    }

    // Test Case 1: Successful Bid Placement
    @Test
    public void testPlaceBid_Success() {
        UUID userId = UUID.randomUUID();
        BidRegistrationRequest bidRequest = new BidRegistrationRequest();
        bidRequest.setSlotId(UUID.randomUUID());
        bidRequest.setBidAmount(new BigDecimal("150.00"));

        Bid bid = new Bid();
        bid.setBidId(UUID.randomUUID());

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(userId);
            when(bidService.placeBid(bidRequest, userId)).thenReturn(bid);

            // Manually validate the request
            BindingResult bindingResult = new BeanPropertyBindingResult(bidRequest, "bidRequest");
            validator.validate(bidRequest, bindingResult);

            assertFalse(bindingResult.hasErrors());

            ResponseEntity<?> response = bidController.placeBid(bidRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Bid placed successfully", response.getBody());
            verify(bidService, times(1)).placeBid(bidRequest, userId);
        }
    }

    // Test Case 2: Auction Slot Not Found
    @Test
    public void testPlaceBid_AuctionSlotNotFound() {
        UUID userId = UUID.randomUUID();
        BidRegistrationRequest bidRequest = new BidRegistrationRequest();
        bidRequest.setSlotId(UUID.randomUUID());
        bidRequest.setBidAmount(new BigDecimal("150.00"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(userId);
            when(bidService.placeBid(bidRequest, userId))
                    .thenThrow(new NoSuchElementException("Auction slot not found"));

            // Manually validate the request
            BindingResult bindingResult = new BeanPropertyBindingResult(bidRequest, "bidRequest");
            validator.validate(bidRequest, bindingResult);

            assertFalse(bindingResult.hasErrors());

            ResponseEntity<?> response = bidController.placeBid(bidRequest);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Auction slot not found", response.getBody());
            verify(bidService, times(1)).placeBid(bidRequest, userId);
        }
    }

    // Test Case 3: User Not Found
    @Test
    public void testPlaceBid_UserNotFound() {
        UUID userId = UUID.randomUUID();
        BidRegistrationRequest bidRequest = new BidRegistrationRequest();
        bidRequest.setSlotId(UUID.randomUUID());
        bidRequest.setBidAmount(new BigDecimal("150.00"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(userId);
            when(bidService.placeBid(bidRequest, userId))
                    .thenThrow(new NoSuchElementException("User not found"));

            // Manually validate the request
            BindingResult bindingResult = new BeanPropertyBindingResult(bidRequest, "bidRequest");
            validator.validate(bidRequest, bindingResult);

            assertFalse(bindingResult.hasErrors());

            ResponseEntity<?> response = bidController.placeBid(bidRequest);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("User not found", response.getBody());
            verify(bidService, times(1)).placeBid(bidRequest, userId);
        }
    }

    // Test Case 4: Auction Slot Not Active
    @Test
    public void testPlaceBid_AuctionSlotNotActive() {
        UUID userId = UUID.randomUUID();
        BidRegistrationRequest bidRequest = new BidRegistrationRequest();
        bidRequest.setSlotId(UUID.randomUUID());
        bidRequest.setBidAmount(new BigDecimal("150.00"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(userId);

            when(bidService.placeBid(bidRequest, userId))
                    .thenThrow(new IllegalArgumentException("Auction slot is not active"));

            // Manually validate the request
            BindingResult bindingResult = new BeanPropertyBindingResult(bidRequest, "bidRequest");
            validator.validate(bidRequest, bindingResult);

            assertFalse(bindingResult.hasErrors());

            ResponseEntity<?> response = bidController.placeBid(bidRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Auction slot is not active", response.getBody());
            verify(bidService, times(1)).placeBid(bidRequest, userId);
        }
    }

    // Test Case 5: Bid Amount Less Than or Equal to Base Price
    @Test
    public void testPlaceBid_BidAmountTooLow() {
        UUID userId = UUID.randomUUID();
        BidRegistrationRequest bidRequest = new BidRegistrationRequest();
        bidRequest.setSlotId(UUID.randomUUID());
        bidRequest.setBidAmount(new BigDecimal("50.00"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(userId);

            when(bidService.placeBid(bidRequest, userId))
                    .thenThrow(new IllegalArgumentException("Bid amount must be higher than the current base price 100.00"));

            // Manually validate the request
            BindingResult bindingResult = new BeanPropertyBindingResult(bidRequest, "bidRequest");
            validator.validate(bidRequest, bindingResult);

            assertFalse(bindingResult.hasErrors());

            ResponseEntity<?> response = bidController.placeBid(bidRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Bid amount must be higher than the current base price 100.00", response.getBody());
            verify(bidService, times(1)).placeBid(bidRequest, userId);
        }
    }

    // Test Case 6: Invalid Bid Amount (Negative Value)
    @Test
    public void testPlaceBid_InvalidBidAmount_NegativeValue() {
        UUID userId = UUID.randomUUID();
        BidRegistrationRequest bidRequest = new BidRegistrationRequest();
        bidRequest.setSlotId(UUID.randomUUID());
        bidRequest.setBidAmount(new BigDecimal("-10.00"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(userId);

            // Manually validate the request
            BindingResult bindingResult = new BeanPropertyBindingResult(bidRequest, "bidRequest");
            validator.validate(bidRequest, bindingResult);

            assertTrue(bindingResult.hasErrors());

            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();

            ResponseEntity<?> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid bid request data");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invalid bid request data", response.getBody());
            verifyNoInteractions(bidService);
        }
    }

    // Test Case 7: Invalid Bid Amount (Null Value)
    @Test
    public void testPlaceBid_InvalidBidAmount_NullValue() {
        UUID userId = UUID.randomUUID();
        BidRegistrationRequest bidRequest = new BidRegistrationRequest();
        bidRequest.setSlotId(UUID.randomUUID());
        bidRequest.setBidAmount(null);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(userId);

            // Manually validate the request
            BindingResult bindingResult = new BeanPropertyBindingResult(bidRequest, "bidRequest");
            validator.validate(bidRequest, bindingResult);

            assertTrue(bindingResult.hasErrors());

            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();

            ResponseEntity<?> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid bid request data");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invalid bid request data", response.getBody());
            verifyNoInteractions(bidService);
        }
    }

    // Test Case 8: Invalid Slot ID (Null Value)
    @Test
    public void testPlaceBid_InvalidSlotId_NullValue() {
        UUID userId = UUID.randomUUID();
        BidRegistrationRequest bidRequest = new BidRegistrationRequest();
        bidRequest.setSlotId(null);
        bidRequest.setBidAmount(new BigDecimal("150.00"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(userId);

            // Manually validate the request
            BindingResult bindingResult = new BeanPropertyBindingResult(bidRequest, "bidRequest");
            validator.validate(bidRequest, bindingResult);

            assertTrue(bindingResult.hasErrors());

            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();

            ResponseEntity<?> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid bid request data");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invalid bid request data", response.getBody());
            verifyNoInteractions(bidService);
        }
    }

    // Test Case 9: Unauthenticated User
    @Test
    public void testPlaceBid_UnauthenticatedUser() {
        BidRegistrationRequest bidRequest = new BidRegistrationRequest();
        bidRequest.setSlotId(UUID.randomUUID());
        bidRequest.setBidAmount(new BigDecimal("150.00"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(null);

            // Manually validate the request
            BindingResult bindingResult = new BeanPropertyBindingResult(bidRequest, "bidRequest");
            validator.validate(bidRequest, bindingResult);

            assertFalse(bindingResult.hasErrors());

            ResponseEntity<?> response = bidController.placeBid(bidRequest);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("User not authenticated", response.getBody());
            verifyNoInteractions(bidService);
        }
    }

    // Test Case 10: Internal Server Error
    @Test
    public void testPlaceBid_InternalServerError() {
        UUID userId = UUID.randomUUID();
        BidRegistrationRequest bidRequest = new BidRegistrationRequest();
        bidRequest.setSlotId(UUID.randomUUID());
        bidRequest.setBidAmount(new BigDecimal("150.00"));

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserUUID).thenReturn(userId);

            when(bidService.placeBid(bidRequest, userId))
                    .thenThrow(new RuntimeException("Unexpected error"));

            // Manually validate the request
            BindingResult bindingResult = new BeanPropertyBindingResult(bidRequest, "bidRequest");
            validator.validate(bidRequest, bindingResult);

            assertFalse(bindingResult.hasErrors());

            ResponseEntity<?> response = bidController.placeBid(bidRequest);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("An unexpected error occurred.", response.getBody());
            verify(bidService, times(1)).placeBid(bidRequest, userId);
        }
    }
}
