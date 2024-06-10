package com.example.dentalclinicschedulingplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "system_admin")
public class SystemAdmin {
    @Id
    private Long adminId;
    @Column(updatable = false, unique = true)
    private String username;
    @Column(updatable = false)
    private String password;
    private boolean status;
}
