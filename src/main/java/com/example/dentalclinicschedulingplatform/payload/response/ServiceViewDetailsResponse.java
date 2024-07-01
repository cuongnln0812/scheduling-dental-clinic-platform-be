package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServiceViewDetailsResponse {
    private Long id;
    private String serviceName;
    private String description;
    private String unitOfPrice;
    private Float minimumPrice;
    private Float maximumPrice;
    private int duration;
    private String serviceType;
    private boolean status;
}
