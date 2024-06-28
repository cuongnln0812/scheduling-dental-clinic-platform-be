package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovedBranchResponse {
    private String branchName;
    private String address;
    private String city;
    private String phone;
    private boolean isMain;
    private ClinicStatus status;
}
