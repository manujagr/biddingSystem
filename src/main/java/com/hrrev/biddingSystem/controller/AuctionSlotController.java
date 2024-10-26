package com.hrrev.biddingSystem.controller;

import com.hrrev.biddingSystem.dto.AuctionSlotRegistrationRequest;
import com.hrrev.biddingSystem.dto.AuctionSlotResponse;
import com.hrrev.biddingSystem.exception.UnauthorizedException;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Authentication;
import com.hrrev.biddingSystem.service.AuctionSlotService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auction-slots")
public class AuctionSlotController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionSlotController.class);

    private final AuctionSlotService auctionSlotService;

    @Autowired
    public AuctionSlotController(AuctionSlotService auctionSlotService) {
        this.auctionSlotService = auctionSlotService;
    }

    /**
     * Endpoint to register a new Auction Slot.
     *
     * @param slotRequest    The request payload containing auction slot details.
     * @param authentication The authentication information of the vendor.
     * @return ResponseEntity containing the created AuctionSlotResponse.
     */
    @PostMapping
    public ResponseEntity<?> registerAuctionSlot(@Valid @RequestBody AuctionSlotRegistrationRequest slotRequest,
                                                 Authentication authentication) {
        try {
            UUID userId = getVendorIdFromAuth(authentication);
            AuctionSlot slot = auctionSlotService.scheduleAuctionSlot(slotRequest, userId);
            AuctionSlotResponse slotResponse = new AuctionSlotResponse(slot);
            return ResponseEntity.status(HttpStatus.CREATED).body(slotResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint to retrieve all active Auction Slots.
     *
     * @return ResponseEntity containing a list of AuctionSlotResponse.
     */
    @GetMapping("/active")
    public ResponseEntity<List<AuctionSlotResponse>> getActiveAuctionSlots() {
        logger.info("Received request to fetch active auction slots.");

        // Retrieve Active Auction Slots
        List<AuctionSlot> slots = auctionSlotService.getActiveAuctionSlots();

        logger.debug("Number of active auction slots retrieved: {}", slots.size());

        // Convert to Response DTOs
        List<AuctionSlotResponse> slotResponses = slots.stream()
                .map(AuctionSlotResponse::new)
                .collect(Collectors.toList());

        logger.info("Returning {} active auction slots.", slotResponses.size());

        return ResponseEntity.ok(slotResponses);
    }

    /**
     * Helper method to extract vendor ID from Authentication.
     *
     * @param authentication The authentication information.
     * @return UUID representing the vendor's user ID.
     * @throws UnauthorizedException if authentication is invalid.
     */
    private UUID getVendorIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getUserId() == null) {
            logger.error("Unauthorized access attempt detected. Authentication details are missing.");
            throw new UnauthorizedException("User is not authenticated.", "AUTH_401");
        }

        UUID userId = authentication.getUserId();
        logger.debug("Extracted user ID from authentication: {}", userId);
        return userId;
    }
}
