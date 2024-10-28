package com.intuit.biddingSystem.repository;

import com.intuit.biddingSystem.model.User;
import com.intuit.biddingSystem.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    Optional<Vendor> findByUser(User user);
    //Optional<Vendor> findById(UUID userId);
}

