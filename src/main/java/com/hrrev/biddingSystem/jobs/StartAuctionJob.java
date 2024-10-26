package com.hrrev.biddingSystem.jobs;

import com.hrrev.biddingSystem.events.AuctionStartedEvent;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StartAuctionJob implements Job {

    @Autowired
    private AuctionSlotRepository auctionSlotRepository;

    @Autowired
    private KafkaTemplate<String, AuctionStartedEvent> kafkaTemplate;

    @Override
    public void execute(JobExecutionContext context) {
        UUID slotId = UUID.fromString(context.getMergedJobDataMap().getString("slotId"));
        AuctionSlot slot = auctionSlotRepository.findById(slotId).orElse(null);
        if (slot != null && slot.getStatus() == AuctionSlot.SlotStatus.SCHEDULED) {
            slot.setStatus(AuctionSlot.SlotStatus.ACTIVE);
            auctionSlotRepository.save(slot);

            // Publish event to Kafka
            AuctionStartedEvent event = new AuctionStartedEvent(slot.getSlotId(), slot.getProduct().getProductId());
            kafkaTemplate.send("auction-started", event);
        }
    }
}