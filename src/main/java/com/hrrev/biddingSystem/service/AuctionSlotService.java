package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.dto.AuctionSlotRegistrationRequest;
import com.hrrev.biddingSystem.jobs.EndAuctionJob;
import com.hrrev.biddingSystem.jobs.StartAuctionJob;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Product;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.ProductRepository;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AuctionSlotService {

    private final AuctionSlotRepository auctionSlotRepository;
    private final ProductRepository productRepository;
    private final Scheduler scheduler;

    @Autowired
    public AuctionSlotService(AuctionSlotRepository auctionSlotRepository, ProductRepository productRepository, Scheduler scheduler) {
        this.auctionSlotRepository = auctionSlotRepository;
        this.productRepository = productRepository;
        this.scheduler = scheduler;
    }

    public AuctionSlot scheduleAuctionSlot(AuctionSlotRegistrationRequest slotRequest, UUID userId) throws Exception {

        Product product = productRepository.findById(slotRequest.getProductId())
                .orElseThrow(() -> new Exception("Product not found"));

        // Verify the product belongs to the vendor
        if (!product.getVendor().getVendorId().equals(userId)) {
            throw new Exception("Unauthorized: Vendor does not own this product");
        }

        // Check if an auction slot already exists for this product
        if (!auctionSlotRepository.findByProductAndStatus(product, AuctionSlot.SlotStatus.ACTIVE).isEmpty()) {
            throw new Exception("Active auction slot already exists for this product");
        }

        // Validate timing constraints
        validateAuctionSlotTiming(slotRequest.getStartTime(), slotRequest.getEndTime());

        // Create a new AuctionSlot entity
        AuctionSlot slot = new AuctionSlot();
        slot.setProduct(product);
        slot.setStartTime(slotRequest.getStartTime());
        slot.setEndTime(slotRequest.getEndTime());

        // Set auction slot status based on current time
        LocalDateTime now = LocalDateTime.now();
        if (slot.getStartTime().isAfter(now)) {
            slot.setStatus(AuctionSlot.SlotStatus.SCHEDULED);
        } else if (slot.getStartTime().isBefore(now) && slot.getEndTime().isAfter(now)) {
            slot.setStatus(AuctionSlot.SlotStatus.ACTIVE);
        } else {
            throw new Exception("Invalid auction slot time range");
        }

        // Save the AuctionSlot
        AuctionSlot auctionSlot = auctionSlotRepository.save(slot);

        // Schedule the start and end jobs
        scheduleStartAuctionJob(auctionSlot);
        scheduleEndAuctionJob(auctionSlot);

        return auctionSlot;
    }

    private void scheduleStartAuctionJob(AuctionSlot slot) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(StartAuctionJob.class)
                .withIdentity("startAuctionJob-" + slot.getSlotId(), "auction-jobs")
                .usingJobData("slotId", slot.getSlotId().toString())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("startAuctionTrigger-" + slot.getSlotId(), "auction-triggers")
                .startAt(Date.from(slot.getStartTime().atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    private void scheduleEndAuctionJob(AuctionSlot slot) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(EndAuctionJob.class)
                .withIdentity("endAuctionJob-" + slot.getSlotId(), "auction-jobs")
                .usingJobData("slotId", slot.getSlotId().toString())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("endAuctionTrigger-" + slot.getSlotId(), "auction-triggers")
                .startAt(Date.from(slot.getEndTime().atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    private void validateAuctionSlotTiming(LocalDateTime startTime, LocalDateTime endTime) throws Exception {
        // Check that the auction slot duration is at least one day
        if (Duration.between(startTime, endTime).toDays() < 1) {
            throw new Exception("Auction slot duration must be at least 1 day.");
        }

        // Check that start and end times are multiples of 30 minutes
        if (startTime.getMinute() % 30 != 0 || endTime.getMinute() % 30 != 0) {
            throw new Exception("Auction slot timings must be in multiples of 30 minutes.");
        }
    }

    public List<AuctionSlot> getActiveAuctionSlots() {
        return auctionSlotRepository.findByStatus(AuctionSlot.SlotStatus.ACTIVE);
    }
    // Additional methods
}
