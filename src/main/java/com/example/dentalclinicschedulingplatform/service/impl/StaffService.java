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
import org.springframework.mail.SimpleMailMessage;
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


    @Override
    public StaffResponse viewStaff(Long id) {
        try {
            ClinicStaff clinicStaff = iStaffRepository.findById(id)
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Staff not found");
                    });
            //return modelMapper.map(clinicStaff, StaffResponse.class);
            return new StaffResponse(clinicStaff);
        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public StaffResponse createStaff(UserInformationRes userInformationRes, StaffCreateRequest request) {
        try {
            //log.info("role: {}", userInformationRes.getRole());
            if(!userInformationRes.getRole().equals(UserType.OWNER.toString())){
                throw new ApiException(HttpStatus.FORBIDDEN, "Do not have permission");
            }
            Optional<ClinicOwner> clinicOwner = Optional.ofNullable(iOwnerRepository.findByUsernameOrEmail(userInformationRes.getUsername(), userInformationRes.getEmail())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic owner not found");
                    }));
            ClinicBranch clinicBranch = iClinicBranchRepository.findById(request.getClinicBranchId())
                    .orElseThrow(() -> {
                        throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not found");
                    });

//            log.info("id branch {}", request.getClinicBranchId().toString());
//            log.info("id owner {}", clinicOwner.get().getId());
            if(!iClinicBranchRepository.findByBranchIdAndOwnerId(request.getClinicBranchId(), clinicOwner.get().getId()).isPresent())
                throw new ApiException(HttpStatus.NOT_FOUND, "Clinic branch not belong to this owner");
            if (authenticateService.isUsernameOrEmailExisted(request.getUsername(), request.getEmail())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Email or user name is existed");
            } else {
                ClinicStaff clinicStaff = new ClinicStaff();
                clinicStaff.setFullName(request.getFullName());
                clinicStaff.setEmail(request.getEmail());
                clinicStaff.setUsername(request.getUsername());
                //clinicStaff.setPassword(passwordEncoder.encode(request.getPassword()));
                clinicStaff.setPhone(request.getPhone());
                clinicStaff.setDob(request.getDob());
                clinicStaff.setAddress(request.getAddress());
                clinicStaff.setGender(request.getGender());
                clinicStaff.setAvatar(request.getAvatar());
                clinicStaff.setClinicBranch(clinicBranch);
                clinicStaff.setStatus(Status.PENDING);

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
            //log.info("role {}", role.toString());
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
            if(!clinicStaff.getStatus().equals(Status.ACTIVE)) throw new ApiException(HttpStatus.CONFLICT, "Clinic staff not active");

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
            if(StringUtils.isNotBlank(request.getUsername())) {
                if (authenticateService.isUsernameOrEmailExisted(request.getUsername(), clinicStaff.getEmail())) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Username is existed");
                }
                clinicStaff.setUsername(request.getUsername());
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
            if(!clinicStaff.getStatus().equals(Status.ACTIVE) && !clinicStaff.getStatus().equals(Status.PENDING)){
                throw new ApiException(HttpStatus.CONFLICT, "Clinic staff is already been deactivated");
            } else {
                clinicStaff.setStatus(Status.INACTIVE);
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

        if(!clinicStaff.getStatus().equals(Status.PENDING)) throw new ApiException(HttpStatus.CONFLICT, "Clinic staff not pending");

        if(isApproved){
            clinicStaff.setStatus(Status.ACTIVE);
            String randomPassword = AutomaticGeneratedPassword.generateRandomPassword();
            clinicStaff.setPassword(passwordEncoder.encode(randomPassword));
            clinicStaff = iStaffRepository.save(clinicStaff);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
            message.setTo(clinicStaff.getEmail());
            // Set a meaningful message
            message.setSubject("[F-Dental] - Tài khoản được duyệt và tạo thành công");
            message.setText("Hi, " + clinicStaff.getFullName() + ",\n\n" +
                            "Tài khoản đăng nhập vào hệ thống F-Dental của bạn đã được duyệt và tạo thành công.\n" +
                            "Vui lòng truy cập hệ thống theo thông tin sau:\n" +
                            "• Username: " + clinicStaff.getUsername() + "\n" +
                            "• Password: " + randomPassword + "\n" + // Placeholder for the password
                            "Lưu ý: Vui lòng thay đổi mật khẩu sau khi đăng nhập.\n");
            // Send the email (assuming you have a mailSender bean configured)
            mailSender.send(message);
            return new StaffResponse(clinicStaff);
        } else {
            iStaffRepository.delete(clinicStaff);
        }
        return new StaffResponse(clinicStaff);
    }
}
