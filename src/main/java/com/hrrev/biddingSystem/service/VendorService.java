package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.dto.UserRegistrationRequest;
import com.hrrev.biddingSystem.dto.VendorRegistrationRequest;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.model.Vendor;
import com.hrrev.biddingSystem.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;
    private final UserService userService;

    @Autowired
    public VendorService(VendorRepository vendorRepository, UserService userService) {
        this.vendorRepository = vendorRepository;
        this.userService = userService;
    }

    public Vendor registerVendor(VendorRegistrationRequest vendorRequest) {
        UserRegistrationRequest userRequest = new UserRegistrationRequest();
        userRequest.setUsername(vendorRequest.getUsername());
        userRequest.setEmail(vendorRequest.getEmail());
        userRequest.setPassword(vendorRequest.getPassword());
        User createdUser = userService.registerUser(userRequest);

        // Create the vendor
        Vendor vendor = new Vendor();
        vendor.setUser(createdUser);
        vendor.setCompanyName(vendorRequest.getCompanyName());
        vendor.setContactInfo(vendorRequest.getContactInfo());

        // Save the vendor
        return vendorRepository.save(vendor);
    }

}
