package com.example.dentalclinicschedulingplatform.payload.request;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceChangeStatusRequest {
    @NotNull(message = "Category Id can not be null")
    public Long serviceId;

    @NotNull(message = "Category status can not be null")
    public ClinicStatus serviceStatus;
}
