package com.example.dentalclinicschedulingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clinic_owner")
public class ClinicOwner{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(name = "full_name")
    private String fullName;
    @Column(unique = true)
    private String email;
    private String address;
    private String password;
    @Column(unique = true)
    private String phone;
    private LocalDate dob;
    private String gender;
    private String avatar;
    @Enumerated(EnumType.STRING)
    private ClinicStatus status;

    @OneToOne(mappedBy = "clinicOwner")
    private Clinic clinic;
}
