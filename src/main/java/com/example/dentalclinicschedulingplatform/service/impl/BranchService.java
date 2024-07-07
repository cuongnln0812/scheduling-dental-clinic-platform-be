package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.BranchCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.BranchUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ClinicUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.ClinicRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import com.example.dentalclinicschedulingplatform.service.IBranchService;
import com.example.dentalclinicschedulingplatform.service.IMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.Status;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        branch.setStatus(ClinicStatus.APPROVED);
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
//            if(!clinic.getStatus().equals(ClinicStatus.ACTIVE) || !clinic.getStatus().equals(ClinicStatus.APPROVED))
//                throw new ApiException(HttpStatus.CONFLICT, "Clinic is not active or approved");

            ClinicBranch clinicBranch = branchRepository.findByPhone(request.getPhone());
            if(clinicBranch != null) throw new ApiException(HttpStatus.BAD_REQUEST, "Phone number is existed");
            ClinicBranch clinicBranch1 = branchRepository.findByAddress(request.getAddress());
            if(clinicBranch1 != null) throw new ApiException(HttpStatus.BAD_REQUEST, "Address is used");

            ClinicBranch branch = new ClinicBranch();
            modelMapper.map(request, branch);
            branch.setStatus(ClinicStatus.ACTIVE);
            branch.setClinic(clinic);
            branch.setMain(false);
            branch.setCreatedDate(LocalDateTime.now());

            branchRepository.save(branch);
            return modelMapper.map(branch, BranchDetailResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<BranchSummaryResponse> getBranchByClinic(Long clinicId) {
        try{
            Clinic clinic = clinicRepository.findById(clinicId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));
            List<ClinicBranch> branchList = branchRepository.findAllByClinic_ClinicId(clinic.getClinicId());
            // Map List<ClinicBranch> to List<BranchSummaryResponse>
            return branchList.stream()
                    .map(BranchSummaryResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw e;
        }
    }

    //    @Override
//    @Transactional
//    public BranchDetailResponse approveNewBranch(Long id, boolean isApproved) {
//        if(!authenticateService.getUserInfo().getRole().equals(UserType.ADMIN.toString())){
//            throw new ApiException(HttpStatus.FORBIDDEN, "Do not have permission");
//        }
//        ClinicBranch clinicBranch = branchRepository.findById(id)
//                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found"));
//        Clinic clinic = clinicRepository.findById(clinicBranch.getClinic().getClinicId())
//                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));
//        ClinicOwner owner = ownerRepository.findById(clinic.getClinicOwner().getId())
//                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner not found"));;
//        if (!clinicBranch.getStatus().equals(ClinicStatus.PENDING))
//            throw new ApiException(HttpStatus.CONFLICT, "Branch status must be pending to be approved");
//        if(isApproved) {
//            clinicBranch.setStatus(ClinicStatus.ACTIVE);
//            branchRepository.save(clinicBranch);
//            mailService.sendBranchRequestApprovalMail(owner);
//            return modelMapper.map(clinicBranch, BranchDetailResponse.class);
//        }else {
//            clinicBranch.setStatus(ClinicStatus.DENIED);
//            ClinicBranch branchDenied = clinicBranch;
//            branchRepository.delete(clinicBranch);
//            mailService.sendBranchRequestRejectionMail(owner);
//            return modelMapper.map(branchDenied, BranchDetailResponse.class);
//        }
//    }

    @Override
    public Page<BranchSummaryResponse> getBranchPendingList(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<ClinicBranch> clinicBranches;
        clinicBranches = branchRepository.findAllByStatusAndIsMain(ClinicStatus.PENDING,false, pageRequest);
        //log.info("list:{}", clinicBranches.toList());
        return clinicBranches.map(branch ->
                new BranchSummaryResponse(branch.getBranchId(),
                branch.getBranchName(), branch.getClinic().getClinicName(),
                branch.getAddress(), branch.getCity(), branch.getPhone(), branch.getStatus(), branch.getCreatedDate(), branch.getModifiedDate()));
    }

    @Override
    public BranchSummaryResponse getBranchPendingDetail(Long branchId) {
        ClinicBranch clinicBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found"));
        if(clinicBranch.isMain()) throw new ApiException(HttpStatus.CONFLICT, "This is main clinic not branch");
        if(!clinicBranch.getStatus().equals(ClinicStatus.PENDING)) throw new ApiException(HttpStatus.CONFLICT, "Clinic branch is not pending");
        return modelMapper.map(clinicBranch, BranchSummaryResponse.class);
    }

    @Override
    public ClinicBranch updateMainBranch(ClinicUpdateRequest request) {
        ClinicBranch branch = branchRepository.findByClinic_ClinicId(request.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic main branch", "clinicId", request.getClinicId()));
        if(!branch.isMain())
            throw new ApiException(HttpStatus.NOT_FOUND, "Cannot found the main branch for update!");
        branch.setBranchName(request.getClinicName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setPhone(request.getPhone());
        branch.setStatus(ClinicStatus.ACTIVE);
        return branchRepository.save(branch);
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
            if(!clinicBranch.getStatus().equals(ClinicStatus.ACTIVE)) throw new ApiException(HttpStatus.CONFLICT, "Clinic branch is not active");

            ClinicBranch clinicBranchCheck = branchRepository.findByAddress(request.getAddress());
            if(clinicBranchCheck != null) throw new ApiException(HttpStatus.BAD_REQUEST, "Address is used");

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
            if(!request.getBranchName().isEmpty()) {
                clinicBranch.setBranchName(request.getBranchName());
            }
            if(!request.getAddress().isEmpty()) {
                clinicBranch.setAddress(request.getAddress());
            }
            if(!request.getCity().isEmpty()) {
                clinicBranch.setCity(request.getCity());
            }
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
            if(!clinicBranch.getStatus().equals(ClinicStatus.ACTIVE)) throw new ApiException(HttpStatus.CONFLICT, "Clinic branch is not active");

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

            clinicBranch.setStatus(ClinicStatus.INACTIVE);
            clinicBranch.setModifiedDate(LocalDateTime.now());
            branchRepository.save(clinicBranch);
            return modelMapper.map(clinicBranch, BranchSummaryResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public BranchSummaryResponse reactiveBranch(Long branchId) {
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
            if(!clinicBranch.getStatus().equals(ClinicStatus.INACTIVE)) throw new ApiException(HttpStatus.CONFLICT, "Clinic branch is not inactive");

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

            clinicBranch.setStatus(ClinicStatus.ACTIVE);
            clinicBranch.setModifiedDate(LocalDateTime.now());
            branchRepository.save(clinicBranch);
            return modelMapper.map(clinicBranch, BranchSummaryResponse.class);
        } catch (Exception e) {
            throw e;
        }
    }
}
