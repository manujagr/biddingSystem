package com.intuit.biddingSystem.auction.bid.consumer;



import com.intuit.biddingSystem.auction.bid.model.BidMessage;
import com.intuit.biddingSystem.auction.bid.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BidConsumer {

    private BidService bidService;
    private static final String BIDS_TOPIC = "bids-topic";

    @Autowired
    public BidConsumer(BidService bidService) {
        this.bidService = bidService;
    }

    @KafkaListener(topics = BIDS_TOPIC, groupId = "notification-group")
    public void consume(BidMessage bidMessage) {
        bidService.placeBid(bidMessage);
    }
}
