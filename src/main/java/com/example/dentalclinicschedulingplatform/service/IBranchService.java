package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;

public interface IBranchService {
    ClinicBranch createMainBranch(Clinic clinic);
}
