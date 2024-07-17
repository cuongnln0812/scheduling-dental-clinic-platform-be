package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDashboardResponse {
    Long numberOfAppointments;
    Long numberOfPendingAppointments;
    Long numberOfDoneAppointments;
    Long numberOfCanceledAppointments;
    Long numberOfClinicUsers;
    Long numberOfClinicDentists;
    Long numberOfClinicStaffs;
    Long numberOfClinicFeedbacks;
}
