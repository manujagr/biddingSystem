package com.hrrev.biddingSystem.controller;

import com.hrrev.biddingSystem.dto.VendorRegistrationRequest;
import com.hrrev.biddingSystem.dto.VendorResponse;
import com.hrrev.biddingSystem.model.Vendor;
import com.hrrev.biddingSystem.service.VendorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private static final Logger logger = LoggerFactory.getLogger(VendorController.class);
    private final VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Registers a new vendor.
     *
     * @param request The vendor registration details.
     * @return ResponseEntity containing the created VendorResponse or an error message.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerVendor(@Valid @RequestBody VendorRegistrationRequest request) {
        try {
            Vendor createdVendor = vendorService.registerVendor(request);
            VendorResponse vendorResponse = new VendorResponse(createdVendor);
            logger.info("Vendor registered successfully with ID: {}", createdVendor.getVendorId());
            return ResponseEntity.status(HttpStatus.CREATED).body(vendorResponse);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid vendor registration details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid vendor registration details");

        } catch (NoSuchElementException e) {
            logger.error("Required resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Required resource not found");

        } catch (Exception e) {
            logger.error("An unexpected error occurred during vendor registration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}