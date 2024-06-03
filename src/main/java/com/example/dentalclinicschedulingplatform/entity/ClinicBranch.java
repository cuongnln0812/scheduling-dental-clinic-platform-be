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
@Table(name = "clinic_branch")
public class ClinicBranch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Long id;
    @Column(name = "branch_name")
    private String branchName;
    private String address;
    private String city;
    private String phone;
    @Column(unique = true)
    private String email;
    @Column(name = "opening_time")
    private LocalTime openingTime;
    @Column(name = "closing_time")
    private LocalTime closingTime;
    private Float totalRating;
    @Column(name = "is_main")
    private boolean isMain;
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

    @OneToMany(mappedBy = "clinicBranch", fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;
    @OneToMany(mappedBy = "clinicBranch", fetch = FetchType.LAZY)
    private List<Appointment> appointments;
    @OneToMany(mappedBy = "clinicBranch", fetch = FetchType.LAZY)
    private List<Dentist> dentists;
    @OneToMany(mappedBy = "clinicBranch", fetch = FetchType.LAZY)
    private List<ClinicStaff> staffs;
    @OneToMany(mappedBy = "clinicBranch")
    private List<WorkingHours> workingHours;
    @OneToMany(mappedBy = "clinicBranch")
    private List<Slot> slots;
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;
}
