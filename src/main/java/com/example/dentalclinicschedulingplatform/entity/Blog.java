package com.example.dentalclinicschedulingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "blog")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String thumbnail;
    @Column(name = "publish_date")
    private LocalDate publishDate;
    @Enumerated(EnumType.STRING)
    private ClinicStatus status;
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;
    @CreatedDate
    @Column(name = "created_date",nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedBy
    @Column(name = "modified_by", insertable = false)
    private String modifiedBy;
    @LastModifiedDate
    @Column(name = "modified_date", insertable = false)
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private ClinicStaff staff;
}
