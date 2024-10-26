package com.hrrev.biddingSystem.jobs;

import com.hrrev.biddingSystem.events.AuctionEndedEvent;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.BidRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EndAuctionJob implements Job {

    @Autowired
    private AuctionSlotRepository auctionSlotRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private KafkaTemplate<String, AuctionEndedEvent> kafkaTemplate;

    @Override
    public void execute(JobExecutionContext context) {
        UUID slotId = UUID.fromString(context.getMergedJobDataMap().getString("slotId"));
        AuctionSlot slot = auctionSlotRepository.findById(slotId).orElse(null);
        if (slot != null && slot.getStatus() == AuctionSlot.SlotStatus.ACTIVE) {
            slot.setStatus(AuctionSlot.SlotStatus.COMPLETED);
            auctionSlotRepository.save(slot);

            // Determine winner
            Bid winningBid = bidRepository.findTopBySlotOrderByBidAmountDesc(slot).orElse(null);

            UUID winningBidId = null;
            UUID winnerUserId = null;

            if (winningBid != null) {
                winningBidId = winningBid.getBidId();
                winnerUserId = winningBid.getUser().getUserId();
            }

            // Publish event to Kafka
            AuctionEndedEvent event = new AuctionEndedEvent(
                    slot.getSlotId(),
                    slot.getProduct().getProductId(),
                    winningBidId,
                    winnerUserId
            );
            kafkaTemplate.send("auction-ended", event);
        }
    }
}
