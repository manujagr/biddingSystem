package com.hrrev.biddingSystem.controller;

import com.hrrev.biddingSystem.dto.AuctionSlotRegistrationRequest;
import com.hrrev.biddingSystem.dto.AuctionSlotResponse;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Authentication;
import com.hrrev.biddingSystem.service.AuctionSlotService;
import com.hrrev.biddingSystem.util.SecurityUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
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
            UUID userId = SecurityUtil.getCurrentUserUUID();
            AuctionSlot slot = auctionSlotService.scheduleAuctionSlot(slotRequest, userId);
            AuctionSlotResponse slotResponse = new AuctionSlotResponse(slot);
            logger.info("Auction slot registered successfully for user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(slotResponse);
        } catch (NoSuchElementException e) {
            logger.error("Vendor ID not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found.");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid auction slot details provided: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid auction slot details.");
        } catch (Exception e) {
            logger.error("Unexpected error occurred while registering auction slot: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
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

        try {
            List<AuctionSlot> slots = auctionSlotService.getActiveAuctionSlots();
            List<AuctionSlotResponse> slotResponses = slots.stream()
                    .map(AuctionSlotResponse::new)
                    .collect(Collectors.toList());

            logger.info("Returning {} active auction slots.", slotResponses.size());
            return ResponseEntity.ok(slotResponses);

        } catch (Exception e) {
            logger.error("Error fetching active auction slots: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}