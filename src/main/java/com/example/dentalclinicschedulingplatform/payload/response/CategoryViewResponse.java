package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryViewResponse {
    private Long id;
    private String categoryName;
    private String categoryImage;
    private boolean status;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<ServiceViewDetailsResponse> services;
}
