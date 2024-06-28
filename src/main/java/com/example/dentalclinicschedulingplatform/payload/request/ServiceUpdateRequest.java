package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Length;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceUpdateRequest {
    @NotNull(message = "Service Id cannot be null")
    private Long serviceId;

    @NotBlank(message = "Service name cannot be blank")
    private String serviceName;

    @Column(length = Length.LOB_DEFAULT)
    @Size(max = Length.LOB_DEFAULT, message = "Description length must be less than or equal to " + Length.LOB_DEFAULT)
    private String description;

    @NotBlank(message = "Unit of price cannot be blank")
    private String unitOfPrice;

    @NotNull(message = "Minimum price cannot be null")
    @Positive(message = "Minimum price must be a positive number")
    private Float minimumPrice;

    @NotNull(message = "Maximum price cannot be null")
    @Positive(message = "Maximum price must be a positive number")
    private Float maximumPrice;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration must be a positive number")
    private Integer duration;

    @NotBlank(message = "Service type cannot be blank")
    private String serviceType;

    @NotNull(message = "Category Id cannot be null")
    private Long categoryId;

    @NotNull(message = "ClinicStatus cannot be null")
    private ClinicStatus status;
}
