package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.BranchCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.BranchUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.ClinicRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import com.example.dentalclinicschedulingplatform.service.IBranchService;
import com.example.dentalclinicschedulingplatform.service.IMailService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchService implements IBranchService {

    private final ClinicBranchRepository branchRepository;
    private final ClinicRepository clinicRepository;
    private final OwnerRepository ownerRepository;
    private final IAuthenticateService authenticateService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final IMailService mailService;

    @Override
    public ClinicBranch createMainBranch(Clinic clinic) {
        ClinicBranch branch = new ClinicBranch();
        branch.setBranchName(clinic.getClinicName());
        branch.setAddress(clinic.getAddress());
        branch.setCity(clinic.getCity());
        branch.setPhone(clinic.getPhone());
        branch.setMain(true);
        branch.setClinic(clinic);
        branch.setStatus(Status.APPROVED);
        return branchRepository.save(branch);
    }

    @Override
    public BranchDetailResponse viewBranch(Long id) {
        try{
            ClinicBranch branch = branchRepository.findById(id)
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Branch not found");
                    });
            return modelMapper.map(branch, BranchDetailResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public BranchDetailResponse createBranch(BranchCreateRequest request) {
        try {
            if(!authenticateService.getUserInfo().getRole().equals(UserType.OWNER.toString())){
                throw new ApiException(HttpStatus.FORBIDDEN, "Do not have permission");
            }
            ClinicOwner owner = ownerRepository.findByUsernameOrEmail(authenticateService.getUserInfo().getUsername(), authenticateService.getUserInfo().getEmail())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner does not exist"));
            Clinic clinic = clinicRepository.findByClinicOwnerId(owner.getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));
//            if(!clinic.getStatus().equals(Status.ACTIVE) || !clinic.getStatus().equals(Status.APPROVED))
//                throw new ApiException(HttpStatus.CONFLICT, "Clinic is not active or approved");

            ClinicBranch clinicBranch = branchRepository.findByPhone(request.getPhone());
            if(clinicBranch != null) throw new ApiException(HttpStatus.BAD_REQUEST, "Phone number is existed");

            ClinicBranch branch = new ClinicBranch();
            modelMapper.map(request, branch);
            branch.setStatus(Status.PENDING);
            branch.setClinic(clinic);
            branch.setMain(false);

            branchRepository.save(branch);
            return modelMapper.map(branch, BranchDetailResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public BranchDetailResponse approveNewBranch(Long id, boolean isApproved) {
        if(!authenticateService.getUserInfo().getRole().equals(UserType.ADMIN.toString())){
            throw new ApiException(HttpStatus.FORBIDDEN, "Do not have permission");
        }
        ClinicBranch clinicBranch = branchRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found"));
        Clinic clinic = clinicRepository.findById(clinicBranch.getClinic().getClinicId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));
        ClinicOwner owner = ownerRepository.findById(clinic.getClinicOwner().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner not found"));;
        if (!clinicBranch.getStatus().equals(Status.PENDING))
            throw new ApiException(HttpStatus.CONFLICT, "Branch status must be pending to be approved");
        if(isApproved) {
            clinicBranch.setStatus(Status.ACTIVE);
            branchRepository.save(clinicBranch);
            mailService.sendBranchRequestApprovalMail(owner);
            return modelMapper.map(clinicBranch, BranchDetailResponse.class);
        }else {
            clinicBranch.setStatus(Status.DENIED);
            ClinicBranch branchDenied = clinicBranch;
            branchRepository.delete(clinicBranch);
            mailService.sendBranchRequestRejectionMail(owner);
            return modelMapper.map(branchDenied, BranchDetailResponse.class);
        }
    }

    @Override
    public Page<BranchSummaryResponse> getBranchPendingList(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<ClinicBranch> clinicBranches;
        clinicBranches = branchRepository.findAllByStatusAndIsMain(Status.PENDING,false, pageRequest);
        //log.info("list:{}", clinicBranches.toList());
        return clinicBranches.map(branch ->
                new BranchSummaryResponse(branch.getBranchId(),
                branch.getBranchName(), branch.getClinic().getClinicName(),
                branch.getAddress(), branch.getCity(), branch.getPhone(), branch.getStatus(), branch.getCreatedDate()));
    }

    @Override
    public BranchSummaryResponse getBranchPendingDetail(Long branchId) {
        ClinicBranch clinicBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found"));
        if(clinicBranch.isMain()) throw new ApiException(HttpStatus.CONFLICT, "This is main clinic not branch");
        if(!clinicBranch.getStatus().equals(Status.PENDING)) throw new ApiException(HttpStatus.CONFLICT, "Clinic branch is not pending");
        return modelMapper.map(clinicBranch, BranchSummaryResponse.class);
    }

    @Override
    public BranchSummaryResponse updateBranch(BranchUpdateRequest request) {
        try{
            String role = authenticateService.getUserInfo().getRole();
            if(!role.equals(UserType.OWNER.toString())){
                throw new ApiException(HttpStatus.FORBIDDEN, "Do not have permission");
            }
            Optional<ClinicOwner> clinicOwner = Optional.ofNullable(ownerRepository.findByUsernameOrEmail(authenticateService.getUserInfo().getUsername(), authenticateService.getUserInfo().getEmail())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic owner not found");
                    }));
            Clinic clinic = clinicRepository.findByClinicOwnerId(clinicOwner.get().getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));
            ClinicBranch clinicBranch = branchRepository.findById(request.getId())
                                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found"));
            if(!clinicBranch.getStatus().equals(Status.ACTIVE)) throw new ApiException(HttpStatus.CONFLICT, "Clinic branch is not active");

            List<ClinicBranch> branchList = branchRepository.findAllByClinic_ClinicId(clinic.getClinicId());
            boolean branchExists = false;
            for (ClinicBranch branch : branchList) {
                if (branch.getBranchId().equals(clinicBranch.getBranchId())) {
                    branchExists = true;
                    break;
                }
            }
            if (!branchExists) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not belong to owner");
            }

            ClinicBranch clinicBranch1 = branchRepository.findByPhone(request.getPhone());
            if(clinicBranch1 != null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Phone number is existed");
            } else  {
                clinicBranch.setPhone(request.getPhone());
            }
            clinicBranch.setBranchName(request.getBranchName());
            clinicBranch.setAddress(request.getAddress());
            clinicBranch.setCity(request.getCity());
            clinicBranch.setModifiedDate(LocalDateTime.now());

            branchRepository.save(clinicBranch);
            return modelMapper.map(clinicBranch, BranchSummaryResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public BranchSummaryResponse deleteBranch(Long branchId) {
        try{
            String role = authenticateService.getUserInfo().getRole();
            if(!role.equals(UserType.OWNER.toString())){
                throw new ApiException(HttpStatus.FORBIDDEN, "Do not have permission");
            }
            Optional<ClinicOwner> clinicOwner = Optional.ofNullable(ownerRepository.findByUsernameOrEmail(authenticateService.getUserInfo().getUsername(), authenticateService.getUserInfo().getEmail())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic owner not found");
                    }));
            Clinic clinic = clinicRepository.findByClinicOwnerId(clinicOwner.get().getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));
            ClinicBranch clinicBranch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found"));
            if(!clinicBranch.getStatus().equals(Status.ACTIVE)) throw new ApiException(HttpStatus.CONFLICT, "Clinic branch is not active");

            List<ClinicBranch> branchList = branchRepository.findAllByClinic_ClinicId(clinic.getClinicId());
            boolean branchExists = false;
            for (ClinicBranch branch : branchList) {
                if (branch.getBranchId().equals(clinicBranch.getBranchId())) {
                    branchExists = true;
                    break;
                }
            }
            if (!branchExists) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not belong to owner");
            }

            clinicBranch.setStatus(Status.INACTIVE);
            clinicBranch.setModifiedDate(LocalDateTime.now());
            branchRepository.save(clinicBranch);
            return modelMapper.map(clinicBranch, BranchSummaryResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }
}
