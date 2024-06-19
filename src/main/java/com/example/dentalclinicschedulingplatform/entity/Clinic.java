package com.example.dentalclinicschedulingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "clinic")
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clinic_id")
    private Long clinicId;
    @Column(name = "clinic_name")
    private String clinicName;
    private String address;
    private String city;
    private String phone;
    @Column(unique = true)
    private String email;
    @Column(length = Length.LOB_DEFAULT)
    private String description;
    @Column(name = "website_url")
    private String websiteUrl;
    private String logo;
    @Column(name = "clinic_registration")
    private String clinicRegistration;
    @Column(name = "clinic_image")
    private String clinicImage;
    @Column(name = "total_rating")
    private Float totalRating;
    @Enumerated(EnumType.STRING)
    private Status status;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", referencedColumnName = "owner_id")
    private ClinicOwner clinicOwner;
    @OneToMany(mappedBy = "clinic")
    private List<ClinicBranch> clinicBranch;
    @OneToMany(mappedBy = "clinic")
    private List<Category> categories;
    @OneToMany(mappedBy = "clinic")
    private List<Service> services;
}