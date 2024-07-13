package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicListResponse {
    private Long clinicId;
    private String clinicName;
    private String address;
    private String city;
    private String phone;
    private String ownerName;
    private String logo;
    private String totalRating;
    private Integer feedbackCount;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private ClinicStatus status;
}
