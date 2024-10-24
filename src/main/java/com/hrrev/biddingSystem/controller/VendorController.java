package com.hrrev.biddingSystem.controller;


import com.hrrev.biddingSystem.dto.VendorRegistrationRequest;
import com.hrrev.biddingSystem.dto.VendorResponse;
import com.hrrev.biddingSystem.model.Vendor;
import com.hrrev.biddingSystem.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping("/register")
    public ResponseEntity<?> registerVendor(@Valid @RequestBody VendorRegistrationRequest request) {
        try {
            Vendor createdVendor = vendorService.registerVendor(request);
            VendorResponse vendorResponse = new VendorResponse(createdVendor);
            return ResponseEntity.status(HttpStatus.CREATED).body(vendorResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Additional endpoints
}

