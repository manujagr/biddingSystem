package com.intuit.biddingSystem.controller;

import com.intuit.biddingSystem.dto.BidRegistrationRequest;
import com.intuit.biddingSystem.model.Bid;
import com.intuit.biddingSystem.service.BidService;
import com.intuit.biddingSystem.util.SecurityUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    /**
     * Places a bid on a specified auction slot.
     *
     * @param bidRequest The bid details.
     * @return ResponseEntity indicating the outcome of the bid placement.
     */
    @PostMapping("/slots")
    public ResponseEntity<?> placeBid(@Valid @RequestBody BidRegistrationRequest bidRequest) {
        try {
            UUID userId = SecurityUtil.getCurrentUserUUID();
            if (userId == null) {
                logger.warn("Unauthenticated user attempted to place a bid");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            Bid bid = bidService.placeBid(bidRequest, userId);
            logger.info("Bid placed successfully by user ID: {}", userId);
            return ResponseEntity.ok("Bid placed successfully");

        } catch (NoSuchElementException e) {
            String message = e.getMessage();
            logger.error("Resource not found: {}", message);
            if ("User not found".equals(message)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            } else if ("Auction slot not found".equals(message)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Auction slot not found");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
            }

        } catch (IllegalArgumentException e) {
            logger.error("Invalid bid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            logger.error("An unexpected error occurred while placing the bid: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
