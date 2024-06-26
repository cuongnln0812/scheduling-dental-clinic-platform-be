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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer")
public class Customer{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
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
    private boolean status;

    @OneToMany(mappedBy = "customer")
    private List<Appointment> appointments;
    @OneToMany(mappedBy = "customer")
    private List<Feedback> feedbacks;
    @OneToMany(mappedBy = "customer")
    private List<TreatmentOutcome> treatmentOutcomes;
    @OneToMany(mappedBy = "customer")
    private List<RefreshToken> refreshTokens;
}
