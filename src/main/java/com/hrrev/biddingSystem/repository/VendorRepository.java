package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUser(User user);
}

