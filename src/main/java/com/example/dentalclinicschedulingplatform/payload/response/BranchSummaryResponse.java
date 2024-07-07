package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchSummaryResponse {
    private Long branchId;
    private String branchName;
    private String clinicName;
    private String address;
    private String city;
    private String phone;
    private ClinicStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public BranchSummaryResponse(ClinicBranch clinicBranch) {
        this.branchId = clinicBranch.getBranchId();
        this.branchName = clinicBranch.getBranchName();
        this.clinicName = clinicBranch.getClinic().getClinicName(); // Assuming Clinic has getClinicName method
        this.address = clinicBranch.getAddress();
        this.city = clinicBranch.getCity();
        this.phone = clinicBranch.getPhone();
        this.status = clinicBranch.getStatus();
        this.createdDate = clinicBranch.getCreatedDate();
        this.modifiedDate = clinicBranch.getModifiedDate();
    }
}
