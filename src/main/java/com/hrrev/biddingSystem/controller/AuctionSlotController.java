package com.hrrev.biddingSystem.controller;

import com.hrrev.biddingSystem.dto.AuctionSlotRegistrationRequest;
import com.hrrev.biddingSystem.dto.AuctionSlotResponse;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Authentication;
import com.hrrev.biddingSystem.service.AuctionSlotService;
import jakarta.validation.Valid;
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

    private final AuctionSlotService auctionSlotService;

    @Autowired
    public AuctionSlotController(AuctionSlotService auctionSlotService) {
        this.auctionSlotService = auctionSlotService;
    }

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

    @GetMapping("/active")
    public ResponseEntity<?> getActiveAuctionSlots() {
        List<AuctionSlot> slots = auctionSlotService.getActiveAuctionSlots();
        List<AuctionSlotResponse> slotResponses = slots.stream()
                .map(AuctionSlotResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(slotResponses);
    }

    // Helper method to extract vendor ID from Authentication
    private UUID getVendorIdFromAuth(Authentication authentication) {
        // Returning the UUID userId from Authentication
        return authentication.getUserId();
    }
}
