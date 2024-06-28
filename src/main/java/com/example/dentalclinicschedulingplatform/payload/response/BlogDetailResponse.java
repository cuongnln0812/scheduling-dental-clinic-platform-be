package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStaff;
import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogDetailResponse {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String thumbnail;
    private LocalDate publishDate;
    private ClinicStatus status;
    private String createdBy;
    private LocalDateTime createdDate;
    private String modifiedBy;
    private LocalDateTime modifiedDate;
    private ClinicStaff staff;
}
