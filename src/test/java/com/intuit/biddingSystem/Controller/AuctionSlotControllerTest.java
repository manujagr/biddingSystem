package com.intuit.biddingSystem.Controller;

import com.intuit.biddingSystem.controller.AuctionSlotController;
import com.intuit.biddingSystem.dto.AuctionSlotRegistrationRequest;
import com.intuit.biddingSystem.dto.AuctionSlotResponse;
import com.intuit.biddingSystem.model.AuctionSlot;
import com.intuit.biddingSystem.model.Category;
import com.intuit.biddingSystem.model.Product;
import com.intuit.biddingSystem.service.AuctionSlotService;
import com.intuit.biddingSystem.util.SecurityUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSlotControllerTest {

    private static MockedStatic<SecurityUtil> securityUtilMock;

    private UUID mockUserId;
    private UUID mockProductId;
    private UUID mockCategoryId;

    private AuctionSlotRegistrationRequest validSlotRequest;

    @Mock
    private AuctionSlotService auctionSlotService;

    @InjectMocks
    private AuctionSlotController auctionSlotController;

    @Mock
    private AuctionSlot mockSlot;

    private Validator validator;

    @BeforeAll
    static void setUpStatic() {
        // Initialize static mock for SecurityUtil
        securityUtilMock = mockStatic(SecurityUtil.class);
    }

    @AfterAll
    static void tearDownStatic() {
        // Close static mock after all tests
        securityUtilMock.close();
    }

    @BeforeEach
    void setUp() {
        // Initialize the validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Set up mock data
        mockUserId = UUID.randomUUID();
        mockProductId = UUID.randomUUID();
        mockCategoryId = UUID.randomUUID();

        // Set up validSlotRequest
        validSlotRequest = new AuctionSlotRegistrationRequest();
        validSlotRequest.setProductId(mockProductId);
        validSlotRequest.setStartTime(LocalDateTime.now().plusDays(1));
        validSlotRequest.setEndTime(LocalDateTime.now().plusDays(2));

        // Set up Category, Product, and AuctionSlot
        Category mockCategory = new Category();
        mockCategory.setCategoryId(mockCategoryId);
        mockCategory.setName("Electronics");

        Product mockProduct = new Product();
        mockProduct.setProductId(mockProductId);
        mockProduct.setCategory(mockCategory);
        mockProduct.setName("Sample Product");

        mockSlot = new AuctionSlot();
        mockSlot.setSlotId(UUID.randomUUID());
        mockSlot.setStartTime(validSlotRequest.getStartTime());
        mockSlot.setEndTime(validSlotRequest.getEndTime());
        mockSlot.setStatus(AuctionSlot.SlotStatus.SCHEDULED);
        mockSlot.setProduct(mockProduct); // Ensure product and category are not null

        // Set up static mock behavior for SecurityUtil
        securityUtilMock.when(SecurityUtil::getCurrentUserUUID).thenReturn(mockUserId);
    }

    @Test
    @DisplayName("Register Auction Slot - Success")
    void registerAuctionSlot_Success() {
        when(auctionSlotService.scheduleAuctionSlot(any(AuctionSlotRegistrationRequest.class), eq(mockUserId)))
                .thenReturn(mockSlot);

        ResponseEntity<?> response = auctionSlotController.registerAuctionSlot(validSlotRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(auctionSlotService, times(1)).scheduleAuctionSlot(any(AuctionSlotRegistrationRequest.class), eq(mockUserId));
    }

    @Test
    @DisplayName("Register Auction Slot - Vendor Not Found")
    void registerAuctionSlot_VendorNotFound() {
        when(auctionSlotService.scheduleAuctionSlot(any(AuctionSlotRegistrationRequest.class), eq(mockUserId)))
                .thenThrow(new NoSuchElementException("Vendor not found"));

        ResponseEntity<?> response = auctionSlotController.registerAuctionSlot(validSlotRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Vendor not found.", response.getBody());
        verify(auctionSlotService, times(1)).scheduleAuctionSlot(any(AuctionSlotRegistrationRequest.class), eq(mockUserId));
    }

    @Test
    @DisplayName("Register Auction Slot - Invalid Auction Slot Details")
    void registerAuctionSlot_InvalidSlotDetails() {
        when(auctionSlotService.scheduleAuctionSlot(any(AuctionSlotRegistrationRequest.class), eq(mockUserId)))
                .thenThrow(new IllegalArgumentException("Invalid auction slot details"));

        ResponseEntity<?> response = auctionSlotController.registerAuctionSlot(validSlotRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid auction slot details.", response.getBody());
        verify(auctionSlotService, times(1)).scheduleAuctionSlot(any(AuctionSlotRegistrationRequest.class), eq(mockUserId));
    }

    @Test
    @DisplayName("Register Auction Slot - Unexpected Error")
    void registerAuctionSlot_UnexpectedError() {
        when(auctionSlotService.scheduleAuctionSlot(any(AuctionSlotRegistrationRequest.class), eq(mockUserId)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = auctionSlotController.registerAuctionSlot(validSlotRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred.", response.getBody());
        verify(auctionSlotService, times(1)).scheduleAuctionSlot(any(AuctionSlotRegistrationRequest.class), eq(mockUserId));
    }

    @Test
    @DisplayName("Get Active Auction Slots - Success")
    void getActiveAuctionSlots_Success() {
        List<AuctionSlot> activeSlots = List.of(mockSlot);
        when(auctionSlotService.getActiveAuctionSlots()).thenReturn(activeSlots);

        ResponseEntity<List<AuctionSlotResponse>> response = auctionSlotController.getActiveAuctionSlots();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(auctionSlotService, times(1)).getActiveAuctionSlots();
    }

    @Test
    @DisplayName("Get Active Auction Slots - No Active Slots")
    void getActiveAuctionSlots_NoActiveSlots() {
        when(auctionSlotService.getActiveAuctionSlots()).thenReturn(new ArrayList<>());

        ResponseEntity<List<AuctionSlotResponse>> response = auctionSlotController.getActiveAuctionSlots();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(auctionSlotService, times(1)).getActiveAuctionSlots();
    }

    @Test
    @DisplayName("Get Active Auction Slots - Unexpected Error")
    void getActiveAuctionSlots_UnexpectedError() {
        when(auctionSlotService.getActiveAuctionSlots()).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<List<AuctionSlotResponse>> response = auctionSlotController.getActiveAuctionSlots();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(auctionSlotService, times(1)).getActiveAuctionSlots();
    }
}
