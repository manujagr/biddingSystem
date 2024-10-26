package com.hrrev.biddingSystem.consumers;

import com.hrrev.biddingSystem.events.AuctionStartedEvent;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.model.Product;
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
import java.util.stream.Collectors;

@Component
public class AuctionStartedEventConsumer {

    @Autowired
    private AuctionSlotRepository auctionSlotRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "auction-started", groupId = "notification-group")
    public void consume(AuctionStartedEvent event) {
        // Fetch the AuctionSlot
        AuctionSlot slot = auctionSlotRepository.findById(event.getSlotId()).orElse(null);

        if (slot != null) {
            Product product = slot.getProduct();

            // Fetch all bids for previous auction slots of this product
            Set<Bid> bids = bidRepository.findByProduct(product);

            // Extract unique users from bids
            Set<User> users = bids.stream()
                    .map(Bid::getUser)
                    .collect(Collectors.toSet());

            // Notify users and vendor
            notificationService.notifyAuctionStarted(users, slot);

            // Optionally notify the vendor
            User vendor = product.getVendor().getUser();
            notificationService.notifyVendorAuctionStarted(vendor, slot);
        }
    }
}
