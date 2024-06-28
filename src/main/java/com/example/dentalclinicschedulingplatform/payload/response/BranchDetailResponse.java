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
public class BranchDetailResponse {
    private Long branchId;
    private String branchName;
    private String address;
    private String city;
    private String phone;
    private Float totalRating;
    private boolean isMain;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String clinicName;
    private ClinicStatus status;
}
