package com.intuit.biddingSystem.vendor.dto;


import com.intuit.biddingSystem.user.dto.UserResponse;
import com.intuit.biddingSystem.vendor.model.Vendor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorResponse extends UserResponse {

    private String companyName;
    private String contactInfo;

    public VendorResponse(Vendor vendor) {
        super(vendor.getUser());
        this.companyName = vendor.getCompanyName();
        this.contactInfo = vendor.getContactInfo();
    }
}
