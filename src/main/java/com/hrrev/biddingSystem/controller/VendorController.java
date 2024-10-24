package com.hrrev.biddingSystem.controller;


import com.hrrev.biddingSystem.model.Vendor;
import com.hrrev.biddingSystem.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> registerVendor(@RequestBody Vendor vendor) {
        Vendor createdVendor = vendorService.registerVendor(vendor);
        return ResponseEntity.ok(createdVendor);
    }

    // Additional endpoints
}

