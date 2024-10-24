package com.hrrev.biddingSystem.controller;


import com.hrrev.biddingSystem.dto.BidRegistrationRequest;
import com.hrrev.biddingSystem.model.Authentication;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.service.BidService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bids")
public class BidController {


    private final BidService bidService;

    @Autowired
    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping("/slots/{slotId}")
    public ResponseEntity<?> placeBid(@Valid @RequestBody BidRegistrationRequest bidRequest, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Bid bid = bidService.placeBid(bidRequest, userId);
            return ResponseEntity.ok("Bid placed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper method to extract user ID from Authentication
    private Long getUserIdFromAuth(Authentication authentication) {
        // Implement logic to extract user ID
        return authentication.getUserId(); // Placeholder
    }
}

