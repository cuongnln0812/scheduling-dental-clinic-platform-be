package com.example.dentalclinicschedulingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long id;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "customer_address")
    private String customerAddress;
    @Column(name = "customer_phone")
    private String customerPhone;
    @Column(name = "customer_dob")
    private LocalDate customerDob;
    @Column(name = "customer_age")
    private int customerAge;
    @Column(name = "customer_email")
    private String customerEmail;
    @Column(name = "appointment_date")
    private LocalDate appointmentDate;
    @Column(name = "service_duration")
    private int duration;
    private Status status;
    @CreatedDate
    @Column(name = "created_date",nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @OneToOne(mappedBy = "appointment")
    private TreatmentOutcome treatmentOutcome;
    @OneToOne
    @JoinColumn(name = "slot_id", referencedColumnName = "slot_id")
    private Slot slot;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private ClinicBranch clinicBranch;
    @ManyToOne
    @JoinColumn(name = "dentist_id")
    private Dentist dentist;
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;
}
