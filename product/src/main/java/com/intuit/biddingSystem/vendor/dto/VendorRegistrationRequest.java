package com.intuit.biddingSystem.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorRegistrationRequest {

      @NotBlank
      @Size(min = 3, max = 50)
      private String companyName;

      @NotBlank
      private String contactInfo;

}
