package com.example.dentalclinicschedulingplatform.payload.response;

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
}
