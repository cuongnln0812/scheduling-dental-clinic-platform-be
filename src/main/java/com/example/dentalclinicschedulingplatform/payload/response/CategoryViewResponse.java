package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.Service;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryViewResponse {
    private Long id;
    private String categoryName;
    private boolean status;
    private List<ServiceViewResponse> services;
}
