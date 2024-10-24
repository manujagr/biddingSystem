package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.model.Vendor;
import com.hrrev.biddingSystem.repository.UserRepository;
import com.hrrev.biddingSystem.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;

    @Autowired
    public VendorService(VendorRepository vendorRepository) {

        this.vendorRepository = vendorRepository;
    }

    public Vendor registerVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

}
