package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClinicDetailResponse {
    private Long id;
    private String clinicName;
    private String address;
    private String city;
    private String phone;
    private String email;
    private String description;
    private String websiteUrl;
    private String logo;
    private String clinicRegistration;
    private String clinicImage;
    private Float totalRating;
    private String ownerName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private ClinicStatus status;
}
