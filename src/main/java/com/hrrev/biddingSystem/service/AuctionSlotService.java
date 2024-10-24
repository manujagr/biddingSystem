package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionSlotService {

    @Autowired
    private AuctionSlotRepository auctionSlotRepository;

    public AuctionSlot scheduleAuctionSlot(AuctionSlot slot) {
        // Implement slot scheduling logic
        return auctionSlotRepository.save(slot);
    }

    // Additional methods
}

