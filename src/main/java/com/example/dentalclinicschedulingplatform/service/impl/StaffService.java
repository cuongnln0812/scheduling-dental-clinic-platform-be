package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.ClinicOwner;
import com.example.dentalclinicschedulingplatform.entity.ClinicStaff;

import com.example.dentalclinicschedulingplatform.entity.Status;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.CreateStaffRequest;
import com.example.dentalclinicschedulingplatform.payload.response.StaffResponse;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.repository.StaffRepository;
import com.example.dentalclinicschedulingplatform.service.IStaffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StaffService implements IStaffService {

    private final StaffRepository iStaffRepository;
    private final OwnerRepository iOwnerRepository;
    private final ClinicBranchRepository iClinicBranchRepository;


    @Override
    public ResponseEntity<StaffResponse> createStaff(CreateStaffRequest request) {
        try {
//            ClinicOwner owner = iOwnerRepository.findByEmail(email).orElseThrow(
//                    () -> {
//                        throw new ApiException(HttpStatus.BAD_REQUEST, "Do not permission");
//                    }
//            );
//            ClinicBranch clinicBranch = iClinicBranchRepository.findById(request.getClinicBranchId()).orElseThrow(
//                    () -> {
//                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found");
//                    }
//            );
//            if(iClinicBranchRepository.findAllBelongClinicOwner(clinicBranch.getId(), owner.getId()).isEmpty())
//                throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not belong to this owner");

            if (iStaffRepository.existsByEmailOrUsername(request.getEmail(), request.getFullName())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Email is existed");
            } else {
                ClinicStaff clinicStaff = new ClinicStaff();
                //clinicStaff.setId(1000L);
                clinicStaff.setFullName(request.getFullName());
                clinicStaff.setEmail(request.getEmail());
                clinicStaff.setPassword(request.getPassword());
                clinicStaff.setPhone(request.getPhone());
                clinicStaff.setDob(request.getDob());
                clinicStaff.setGender(request.getGender());
                clinicStaff.setAvatar(request.getAvatar());
                clinicStaff.setClinicBranch(null);
                clinicStaff.setStatus(Status.PENDING);
                //clinicStaff.setClinicBranch(clinicBranch);

                clinicStaff = iStaffRepository.save(clinicStaff);

                return new ResponseEntity<StaffResponse>(new StaffResponse(clinicStaff), HttpStatus.OK);
            }
        }  catch (Exception e) {
            throw e;
        }
    }
}
