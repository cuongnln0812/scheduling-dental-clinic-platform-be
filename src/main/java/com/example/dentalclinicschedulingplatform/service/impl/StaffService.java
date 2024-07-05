package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;

import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.StaffCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.StaffUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.StaffResponse;
import com.example.dentalclinicschedulingplatform.payload.response.StaffSummaryResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.repository.StaffRepository;
import com.example.dentalclinicschedulingplatform.service.IMailService;
import com.example.dentalclinicschedulingplatform.service.IStaffService;
import com.example.dentalclinicschedulingplatform.utils.AutomaticGeneratedPassword;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StaffService implements IStaffService {

    private final JavaMailSender mailSender;
    private final StaffRepository iStaffRepository;
    private final OwnerRepository iOwnerRepository;
    private final ClinicBranchRepository iClinicBranchRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticateService authenticateService;
    private final IMailService mailService;


    @Override
    public StaffResponse viewStaff(Long id) {
        try {
            ClinicStaff clinicStaff = iStaffRepository.findById(id)
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Staff not found");
                    });
            return new StaffResponse(clinicStaff);
        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public StaffResponse createStaff(UserInformationRes userInformationRes, StaffCreateRequest request) {
        try {
            if(!userInformationRes.getRole().equals(UserType.OWNER.toString())){
                throw new ApiException(HttpStatus.FORBIDDEN, "Do not have permission");
            }

            Optional<ClinicStaff> clinicStaffCheckByPhone = iStaffRepository.findByPhone(request.getPhone());
            if(clinicStaffCheckByPhone.isPresent()) throw new ApiException(HttpStatus.CONFLICT, "Phone is existed");


            Optional<ClinicOwner> clinicOwner = Optional.ofNullable(iOwnerRepository.findByUsernameOrEmail(userInformationRes.getUsername(), userInformationRes.getEmail())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic owner not found");
                    }));
            ClinicBranch clinicBranch = iClinicBranchRepository.findById(request.getClinicBranchId())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found");
                    });
            if(!iClinicBranchRepository.findByBranchIdAndOwnerId(request.getClinicBranchId(), clinicOwner.get().getId()).isPresent())
            {
                throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not belong to this owner");
            }
            else {
                ClinicStaff clinicStaff = new ClinicStaff();
                clinicStaff.setFullName(request.getFullName());
                clinicStaff.setEmail(request.getEmail());
                clinicStaff.setPhone(request.getPhone());
                clinicStaff.setDob(request.getDob());
                clinicStaff.setAddress(request.getAddress());
                clinicStaff.setGender(request.getGender());
                clinicStaff.setAvatar(request.getAvatar());
                clinicStaff.setClinicBranch(clinicBranch);
                clinicStaff.setStatus(ClinicStatus.PENDING);

                iStaffRepository.save(clinicStaff);
                return new StaffResponse(clinicStaff);
            }
        }  catch (Exception e) {
            throw e;
        }
    }


    @Override
    public StaffResponse updateStaff(UserInformationRes userInformationRes, StaffUpdateRequest request) {
        try {
            String role = userInformationRes.getRole();
            if(!role.equals(UserType.OWNER.toString()) && !role.equals(UserType.STAFF.toString())){
                throw new ApiException(HttpStatus.FORBIDDEN, "Do not have permission");
            }
            Optional<ClinicOwner> clinicOwner = Optional.ofNullable(iOwnerRepository.findByUsernameOrEmail(userInformationRes.getUsername(), userInformationRes.getEmail())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic owner not found");
                    }));
            ClinicStaff clinicStaff = iStaffRepository.findById(request.getId())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic staff not found");
                    });
            if(!clinicStaff.getStatus().equals(ClinicStatus.ACTIVE)) throw new ApiException(HttpStatus.CONFLICT, "Clinic staff not active");

            List<ClinicStaff> staffList = iStaffRepository.findAllStaffByOwnerId(clinicOwner.get().getId());
            boolean staffExists = false;
            for (ClinicStaff staff : staffList) {
                if (staff.getId().equals(clinicStaff.getId())) {
                    staffExists = true;
                    break;
                }
            }
            if (!staffExists) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Clinic staff not belong to owner");
            }

            if(StringUtils.isNotBlank(request.getFullName())){
                clinicStaff.setFullName(request.getFullName());
            }
            if(StringUtils.isNotBlank(request.getPassword())) {
                clinicStaff.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if(StringUtils.isNotBlank(request.getPhone())) {
                clinicStaff.setPhone(request.getPhone());
            }
            if(request.getDob() != null){
                clinicStaff.setDob(request.getDob());
            }
            if(StringUtils.isNotBlank(request.getAddress())){
                clinicStaff.setAddress(request.getAddress());
            }
            if(StringUtils.isNotBlank(request.getGender())){
                clinicStaff.setGender(request.getGender());
            }
            if(StringUtils.isNotBlank(request.getAvatar())){
                clinicStaff.setAvatar(request.getAvatar());
            }
            if(StringUtils.isNotBlank(request.getClinicBranchId().toString())) {
                ClinicBranch clinicBranch = iClinicBranchRepository.findById(request.getClinicBranchId())
                        .orElseThrow(() -> {
                            throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found");
                        });
                if(!iClinicBranchRepository.findByBranchIdAndOwnerId(request.getClinicBranchId(), clinicOwner.get().getId()).isPresent())
                    throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not belong to this owner");
                clinicStaff.setClinicBranch(clinicBranch);
            }
            iStaffRepository.save(clinicStaff);
            return new StaffResponse(clinicStaff);

        }  catch (Exception e) {
            throw e;
        }
    }

    @Override
    public StaffResponse deleteStaff(UserInformationRes userInformationRes, Long id) {
        try {
            if(!userInformationRes.getRole().contains(UserType.OWNER.toString())){
                throw new ApiException(HttpStatus.FORBIDDEN, "Do not have permission");
            }

            Optional<ClinicOwner> clinicOwner = Optional.ofNullable(iOwnerRepository.findByUsernameOrEmail(userInformationRes.getUsername(), userInformationRes.getEmail())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic owner not found");
                    }));

            ClinicStaff clinicStaff = iStaffRepository.findById(id)
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic staff not found");
                    });

            List<ClinicStaff> staffList = iStaffRepository.findAllStaffByOwnerId(clinicOwner.get().getId());
            boolean staffExists = false;
            for (ClinicStaff staff : staffList) {
                if (staff.getId().equals(clinicStaff.getId())) {
                    staffExists = true;
                    break;
                }
            }
            if (!staffExists) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Clinic staff not belong to owner");
            }
            if(!clinicStaff.getStatus().equals(ClinicStatus.ACTIVE) && !clinicStaff.getStatus().equals(ClinicStatus.PENDING)){
                throw new ApiException(HttpStatus.CONFLICT, "Clinic staff is already been deactivated");
            } else {
                clinicStaff.setStatus(ClinicStatus.INACTIVE);
            }

            iStaffRepository.save(clinicStaff);
            return new StaffResponse(clinicStaff);

        }  catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<StaffSummaryResponse> viewAllStaffByOwner(UserInformationRes userInformationRes, int page, int size) {
        try {
            Pageable pageRequest = PageRequest.of(page, size);
            List<StaffSummaryResponse> staffSummaryResponses = new ArrayList<>();

            Optional<ClinicOwner> clinicOwner = Optional.ofNullable(iOwnerRepository.findByUsernameOrEmail(userInformationRes.getUsername(), userInformationRes.getEmail())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic owner not found");
                    }));
            Page<ClinicStaff> staffs = iStaffRepository.findAllStaffByOwnerId(clinicOwner.get().getId(), pageRequest);
            for (ClinicStaff clinicStaff : staffs) {
                staffSummaryResponses.add(new StaffSummaryResponse(clinicStaff));
            }
            staffSummaryResponses.sort(Comparator.comparing(StaffSummaryResponse::getId));
            return staffSummaryResponses;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<StaffSummaryResponse> viewAllStaffByClinicBranch(UserInformationRes userInformationRes, Long branchId, int page, int size) {
        try {
            Pageable pageRequest = PageRequest.of(page, size);
            List<StaffSummaryResponse> staffSummaryResponses = new ArrayList<>();

            Optional<ClinicOwner> clinicOwner = Optional.ofNullable(iOwnerRepository.findByUsernameOrEmail(userInformationRes.getUsername(), userInformationRes.getEmail())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic owner not found");
                    }));
            if(iClinicBranchRepository.findById(branchId).isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found");
            if(iClinicBranchRepository.findByBranchIdAndOwnerId(branchId, clinicOwner.get().getId()).isEmpty())
                throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not belong to this owner");

            Page<ClinicStaff> staffs = iStaffRepository.findAllByClinicBranch_BranchId(branchId, pageRequest);
            for (ClinicStaff clinicStaff : staffs) {
                staffSummaryResponses.add(new StaffSummaryResponse(clinicStaff));
            }
            staffSummaryResponses.sort(Comparator.comparing(StaffSummaryResponse::getId));
            return staffSummaryResponses;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<StaffSummaryResponse> viewAll(int page, int size) {
        try {
            Pageable pageRequest = PageRequest.of(page, size);
            List<StaffSummaryResponse> staffSummaryResponses = new ArrayList<>();

            Page<ClinicStaff> staffs = iStaffRepository.findAll(pageRequest);
            for (ClinicStaff clinicStaff : staffs) {
                staffSummaryResponses.add(new StaffSummaryResponse(clinicStaff));
            }
            staffSummaryResponses.sort(Comparator.comparing(StaffSummaryResponse::getId));
            return staffSummaryResponses;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public StaffResponse approveStaff(Long staffId, boolean isApproved) {


        ClinicStaff clinicStaff = iStaffRepository.findById(staffId)
                .orElseThrow(() -> {
                    throw new ApiException(HttpStatus.NOT_FOUND, "Clinic staff not found");
                });
        ClinicStaff clinicStaffDenied = clinicStaff;

        if(!clinicStaff.getStatus().equals(ClinicStatus.PENDING)) throw new ApiException(HttpStatus.CONFLICT, "Clinic staff not pending");

        ClinicBranch clinicBranch = iClinicBranchRepository.findById(clinicStaff.getClinicBranch().getBranchId())
                .orElseThrow(() -> {
                   throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found");
                });

        if(isApproved){
            clinicStaff.setStatus(ClinicStatus.ACTIVE);
            clinicStaff.setUsername("staff" + clinicStaff.getId());
            String randomPassword = AutomaticGeneratedPassword.generateRandomPassword();
            clinicStaff.setPassword(passwordEncoder.encode(randomPassword));
            clinicStaff = iStaffRepository.save(clinicStaff);
            mailService.sendStaffRequestApprovalMail(clinicStaff, clinicBranch.getBranchName(), randomPassword);
            return new StaffResponse(clinicStaff);
        } else {
            clinicStaffDenied.setStatus(ClinicStatus.DENIED);
            mailService.sendStaffRequestRejectionMail(clinicStaffDenied, clinicBranch.getBranchName());
            iStaffRepository.delete(clinicStaff);
            return new StaffResponse(clinicStaffDenied);
        }
    }

    @Override
    public Page<StaffSummaryResponse> getStaffPendingList(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<ClinicStaff> clinicStaffs;
        clinicStaffs = iStaffRepository.findAllStaffByStatus(ClinicStatus.PENDING, pageRequest);
        return clinicStaffs.map(staff ->
                new StaffSummaryResponse(staff.getId(),
                staff.getFullName(), staff.getUsername(), staff.getEmail(), staff.getPhone(), staff.getGender(),
                            staff.getClinicBranch().getBranchName(), staff.getStatus()));
    }
}
