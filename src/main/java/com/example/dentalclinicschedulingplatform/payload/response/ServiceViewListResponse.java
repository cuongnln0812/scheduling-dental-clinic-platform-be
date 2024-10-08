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
public class ServiceViewListResponse {
    private Long id;
    private String serviceName;
    private boolean status;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String categoryName;
    private Long clinicId;
}
