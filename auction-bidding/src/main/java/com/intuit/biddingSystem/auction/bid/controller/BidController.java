package com.intuit.biddingSystem.auction.bid.controller;

import com.intuit.biddingSystem.auction.bid.dto.BidRegistrationRequest;
import com.intuit.biddingSystem.auction.bid.model.BidMessage;
import com.intuit.biddingSystem.coreUtils.authorization.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private static final Logger logger = LoggerFactory.getLogger(BidController.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String BIDS_TOPIC = "bids-topic";

    @Autowired
    public BidController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
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
            LocalDateTime bidTime = LocalDateTime.now();
            BidMessage bidMessage = new BidMessage(bidRequest.getSlotId(),userId,bidRequest.getBidAmount(),bidTime);
            kafkaTemplate.send(BIDS_TOPIC,bidMessage);
            logger.info("Bid under process for user ID: {}", userId);
            return ResponseEntity.ok("Bid under process");
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