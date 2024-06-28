package com.example.dentalclinicschedulingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Length;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dentist")
public class Dentist{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dentist_id")
    private Long id;
    @Column(name = "full_name")
    private String fullName;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String address;
    private String password;
    @Column(unique = true)
    private String phone;
    private LocalDate dob;
    private String gender;
    @Column(length = Length.LOB_DEFAULT)
    private String description;
    @Column(length = Length.LOB_DEFAULT)
    private String specialty;
    @Column(length = Length.LOB_DEFAULT)
    private String experience;
    private String avatar;
    @Enumerated(EnumType.STRING)
    private ClinicStatus status;

    @OneToMany(mappedBy = "dentist")
    private List<Appointment> appointments;
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private ClinicBranch clinicBranch;
}
