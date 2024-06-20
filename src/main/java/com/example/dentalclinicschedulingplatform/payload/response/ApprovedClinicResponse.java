package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApprovedClinicResponse {
    private String clinicName;
    private String address;
    private String city;
    private String phone;
    private String email;
    private String websiteUrl;
    private String logo;
    private String clinicRegistration;
    private String clinicImage;
    private Status status;
    private ApprovedBranchResponse branchDetail;
    private ApprovedOwnerResponse ownerDetail;
}
