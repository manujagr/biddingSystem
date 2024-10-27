package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.dto.UserRegistrationRequest;
import com.hrrev.biddingSystem.dto.VendorRegistrationRequest;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.model.Vendor;
import com.hrrev.biddingSystem.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);

    private final VendorRepository vendorRepository;
    private final UserService userService;

    @Autowired
    public VendorService(VendorRepository vendorRepository, UserService userService) {
        this.vendorRepository = vendorRepository;
        this.userService = userService;
    }

    public Vendor registerVendor(VendorRegistrationRequest vendorRequest) {
        logger.info("Registering vendor with company name: {}", vendorRequest.getCompanyName());

        try {
            // Create UserRegistrationRequest to register associated user
            UserRegistrationRequest userRequest = new UserRegistrationRequest();
            userRequest.setUsername(vendorRequest.getUsername());
            userRequest.setEmail(vendorRequest.getEmail());
            userRequest.setPassword(vendorRequest.getPassword());
            User createdUser = userService.registerUser(userRequest);

            // Create and save the Vendor entity
            Vendor vendor = new Vendor();
            vendor.setUser(createdUser);
            vendor.setCompanyName(vendorRequest.getCompanyName());
            vendor.setContactInfo(vendorRequest.getContactInfo());

            Vendor savedVendor = vendorRepository.save(vendor);
            logger.info("Vendor registered successfully with ID: {}", savedVendor.getVendorId());

            return savedVendor;

        } catch (DataIntegrityViolationException e) {
            logger.error("Vendor registration failed due to data integrity violation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while registering the vendor: {}", e.getMessage());
            throw new RuntimeException("Failed to register vendor", e);
        }
    }
}