package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Length;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClinicUpdateRequest {
    @NotNull(message = "Clinic id must not be null")
    private Long clinicId;
    @NotBlank(message = "Clinic name must not be empty")
    private String clinicName;
    @NotBlank(message = "Address must not be empty")
    private String address;
    @NotBlank(message = "City must not be empty")
    private String city;
    @NotBlank(message = "Clinic phone must not empty")
    private String phone;
    @NotBlank(message = "Clinic email must not empty")
    private String email;
    @NotBlank(message = "Clinic description must not be empty")
    private String description;
    private String websiteUrl;
    @NotBlank(message = "Clinic logo must not be empty")
    private String logo;
    @NotBlank(message = "Clinic image must not be empty")
    private String clinicImage;
    @NotEmpty(message = "Working hours must not be empty")
    private List<WorkingHoursCreateRequest> workingHours;
}
