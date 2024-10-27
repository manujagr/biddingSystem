package com.hrrev.biddingSystem.controller;

import com.hrrev.biddingSystem.dto.BidRegistrationRequest;
import com.hrrev.biddingSystem.model.Authentication;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.service.BidService;
import com.hrrev.biddingSystem.util.SecurityUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private static final Logger logger = LoggerFactory.getLogger(BidController.class);
    private final BidService bidService;

    @Autowired
    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    /**
     * Places a bid on a specified auction slot.
     *
     * @param bidRequest The bid details.
     * @return ResponseEntity indicating the outcome of the bid placement.
     */
    @PostMapping("/slots/{slotId}")
    public ResponseEntity<?> placeBid(@Valid @RequestBody BidRegistrationRequest bidRequest) {
        try {
            UUID userId = SecurityUtil.getCurrentUserUUID();
            Bid bid = bidService.placeBid(bidRequest, userId);
            logger.info("Bid placed successfully by user ID: {}", userId);
            return ResponseEntity.ok("Bid placed successfully");

        } catch (NoSuchElementException e) {
            logger.error("User ID not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        } catch (IllegalArgumentException e) {
            logger.error("Invalid bid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            logger.error("An unexpected error occurred while placing the bid: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}