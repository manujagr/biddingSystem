package com.hrrev.biddingSystem.consumers;

import com.hrrev.biddingSystem.events.AuctionEndedEvent;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.BidRepository;
import com.hrrev.biddingSystem.repository.UserRepository;
import com.hrrev.biddingSystem.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AuctionEndedEventConsumer {

    @Autowired
    private AuctionSlotRepository auctionSlotRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "auction-ended", groupId = "notification-group")
    public void consume(AuctionEndedEvent event) {
        // Fetch the AuctionSlot
        AuctionSlot slot = auctionSlotRepository.findById(event.getSlotId()).orElse(null);

        if (slot != null) {
            // Fetch bidders
            List<Bid> bids = bidRepository.findBySlot(slot);

            // Extract unique users from bids
            Set<User> bidders = bids.stream()
                    .map(Bid::getUser)
                    .collect(Collectors.toSet());

            // Notify bidders and vendor
            notificationService.notifyAuctionEnded(bidders, slot, event.getWinnerUserId());
        }
    }
}
