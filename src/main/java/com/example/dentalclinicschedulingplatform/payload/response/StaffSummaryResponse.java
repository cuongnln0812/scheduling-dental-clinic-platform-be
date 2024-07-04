package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.ClinicStaff;
import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StaffSummaryResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String clinicBranchName;
    private ClinicStatus status;


    public StaffSummaryResponse(ClinicStaff clinicStaff) {
        this.id = clinicStaff.getId();
        this.fullName = clinicStaff.getFullName();
        this.email = clinicStaff.getEmail();
        this.phone = clinicStaff.getPhone();
        this.gender = clinicStaff.getGender();
        this.clinicBranchName = clinicStaff.getClinicBranch().getBranchName();
        this.status = clinicStaff.getStatus();
    }
}
