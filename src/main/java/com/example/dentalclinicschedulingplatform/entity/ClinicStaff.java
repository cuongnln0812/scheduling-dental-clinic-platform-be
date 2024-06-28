package com.example.dentalclinicschedulingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clinic_staff")
public class ClinicStaff{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
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
    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY)
    private List<Blog> blogs;
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private ClinicBranch clinicBranch;
}
