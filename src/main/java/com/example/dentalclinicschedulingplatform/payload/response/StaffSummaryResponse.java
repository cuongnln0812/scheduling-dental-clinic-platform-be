package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStaff;
import com.example.dentalclinicschedulingplatform.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StaffSummaryResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String gender;
    private String clinicBranchName;
    private Status status;


    public StaffSummaryResponse(ClinicStaff clinicStaff) {
        this.id = clinicStaff.getId();
        this.fullName = clinicStaff.getFullName();
        this.phone = clinicStaff.getPhone();
        this.gender = clinicStaff.getGender();
        this.clinicBranchName = clinicStaff.getClinicBranch().getBranchName();
        this.status = clinicStaff.getStatus();
    }
}
