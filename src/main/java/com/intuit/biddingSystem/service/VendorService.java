package com.intuit.biddingSystem.service;

import com.intuit.biddingSystem.dto.VendorRegistrationRequest;
import com.intuit.biddingSystem.model.User;
import com.intuit.biddingSystem.model.Vendor;
import com.intuit.biddingSystem.repository.UserRepository;
import com.intuit.biddingSystem.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);

    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public VendorService(VendorRepository vendorRepository, UserService userService, UserRepository userRepository) {
        this.vendorRepository = vendorRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public Vendor registerVendor(VendorRegistrationRequest vendorRequest, UUID userId) {
        logger.info("Registering vendor with company name: {}", vendorRequest.getCompanyName());

        try {
//            // Create UserRegistrationRequest to register associated user
//            UserRegistrationRequest userRequest = new UserRegistrationRequest();
//            userRequest.setUsername(vendorRequest.getUsername());
//            userRequest.setEmail(vendorRequest.getEmail());
//            userRequest.setPassword(vendorRequest.getPassword());
//            User createdUser = userService.registerUser(userRequest);
            Optional<User> user = userRepository.findById(userId);
            // Create and save the Vendor entity
            Vendor vendor = new Vendor();
            if(user.isPresent()){
                vendor.setUser(user.get());
                vendor.setCompanyName(vendorRequest.getCompanyName());
                vendor.setContactInfo(vendorRequest.getContactInfo());
                Vendor savedVendor = vendorRepository.save(vendor);
                logger.info("Vendor registered successfully with ID: {}", savedVendor.getVendorId());
            } else {
                logger.error("User is not present in the system");
            }
            return vendor;

        } catch (DataIntegrityViolationException e) {
            logger.error("Vendor registration failed due to data integrity violation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while registering the vendor: {}", e.getMessage());
            throw new RuntimeException("Failed to register vendor", e);
        }
    }

//    /**
//     * Retrieves the Vendor associated with a given User.
//     *
//     * @param user The User entity.
//     * @return The Vendor entity if present, else null.
//     */
//    public Vendor getVendorByUser(User user) {
//        return vendorRepository.findByUser(user).orElse(null);
//    }

}