package com.hrrev.biddingSystem.controller;


import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.service.AuctionSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class AuctionSlotController {

    @Autowired
    private AuctionSlotService auctionSlotService;

    @PostMapping
    public ResponseEntity<?> scheduleAuctionSlot(@RequestBody AuctionSlot slot) {
        AuctionSlot createdSlot = auctionSlotService.scheduleAuctionSlot(slot);
        return ResponseEntity.ok(createdSlot);
    }

    @GetMapping
    public ResponseEntity<?> getActiveAuctionSlots() {
        List<AuctionSlot> slots = auctionSlotService.getActiveAuctionSlots();
        return ResponseEntity.ok(slots);
    }

    // Additional endpoints
}

