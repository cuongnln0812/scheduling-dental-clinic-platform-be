package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.payload.request.WorkingHoursCreateRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClinicUpdateResponse {
    private Long clinicId;
    private String clinicName;
    private String address;
    private String city;
    private String phone;
    private String email;
    private String description;
    private String websiteUrl;
    private String logo;
    private String clinicImage;
    private List<WorkingHoursResponse> workingHours;
}
