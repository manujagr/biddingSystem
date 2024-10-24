package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.dto.AuctionSlotRegistrationRequest;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Product;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionSlotService {

    public AuctionSlotService(AuctionSlotRepository auctionSlotRepository, ProductRepository productRepository) {
        this.auctionSlotRepository = auctionSlotRepository;
        this.productRepository = productRepository;
    }


    private final AuctionSlotRepository auctionSlotRepository;
    private final ProductRepository productRepository;

    public AuctionSlot scheduleAuctionSlot(AuctionSlotRegistrationRequest slotRequest, Long vendorId) throws Exception {

        Product product = productRepository.findById(slotRequest.getProductId())
                .orElseThrow(() -> new Exception("Product not found"));

        // Verify the product belongs to the vendor
        if (!product.getVendor().getVendorId().equals(vendorId)) {
            throw new Exception("Unauthorized: Vendor does not own this product");
        }

        // Check if an auction slot already exists for this product
        if (!auctionSlotRepository.findByProductAndStatus(product, AuctionSlot.SlotStatus.ACTIVE).isEmpty()) {
            throw new Exception("Active auction slot already exists for this product");
        }

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

        // Implement slot scheduling logic
        return auctionSlotRepository.save(slot);
    }

    public List<AuctionSlot> getActiveAuctionSlots() {
        return auctionSlotRepository.findByStatus(AuctionSlot.SlotStatus.ACTIVE);
    }

    // Additional methods
}

