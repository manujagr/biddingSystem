package com.hrrev.biddingSystem.controller;


import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    @Autowired
    private BidService bidService;

    @PostMapping("/slots/{slotId}")
    public ResponseEntity<?> placeBid(
            @PathVariable Long slotId,
            @RequestParam BigDecimal bidAmount,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Bid bid = bidService.placeBid(slotId, userId, bidAmount);
            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper method to extract user ID from Authentication
    private Long getUserIdFromAuth(Authentication authentication) {
        // Implement logic to extract user ID
        return 1L; // Placeholder
    }
}

