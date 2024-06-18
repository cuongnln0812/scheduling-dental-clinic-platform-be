package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.Status;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.service.IBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BranchService implements IBranchService {

    private final ClinicBranchRepository branchRepository;


    @Override
    public void createMainBranch(Clinic clinic) {
        ClinicBranch branch = new ClinicBranch();
        branch.setBranchName(clinic.getClinicName());
        branch.setAddress(clinic.getAddress());
        branch.setCity(clinic.getCity());
        branch.setPhone(clinic.getPhone());
        branch.setMain(true);
        branch.setClinic(clinic);
        branch.setStatus(Status.APPROVED);
        branchRepository.save(branch);
    }
}
