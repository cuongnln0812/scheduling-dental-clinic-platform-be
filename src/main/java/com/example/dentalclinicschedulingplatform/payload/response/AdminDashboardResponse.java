package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponse {
    Long numberOfDentalClinic;
    Long numberOfPendingDentalClinic;
    Long numberOfActiveDentalClinic;
    Long numberOfInactiveDentalClinic;
    Long numberOfClinicUsers;
    Long numberOfClinicDentists;
    Long numberOfClinicStaffs;
    Long numberOfClinicOwners;
    Long numberOfCustomers;
    Long numberOfBlogs;
}
