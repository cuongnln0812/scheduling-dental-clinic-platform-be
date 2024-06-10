package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.Dentist;
import com.example.dentalclinicschedulingplatform.entity.Status;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.DentistCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.DentistUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.DentistDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistListResponse;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.DentistRepository;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import com.example.dentalclinicschedulingplatform.service.IDentistService;
import com.example.dentalclinicschedulingplatform.utils.AutomaticGeneratedPassword;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DentistService implements IDentistService {

    private final JavaMailSender mailSender;
    private final ModelMapper modelMapper;
    private final DentistRepository dentistRepository;
    private final ClinicBranchRepository branchRepository;
    private final IAuthenticateService authenticateService;

    @Override
    public Page<DentistListResponse> getDentistListByBranch(Long branchId, int page, int size, String dir, String by) {
        Sort sort = dir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(by).ascending() : Sort.by(by).descending();
        Pageable pageRequest = PageRequest.of(page, size, sort);
        Page<Dentist> dentistList;
        if(branchId != null){
            dentistList = dentistRepository.findAllByClinicBranch_BranchId(branchId, pageRequest);
        }else dentistList = dentistRepository.findAll(pageRequest);

        return dentistList.map(dentist -> modelMapper.map(dentist,DentistListResponse.class));
    }

    @Override
    public Page<DentistListResponse> getPendingDentistList(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<Dentist> dentistList;
        dentistList = dentistRepository.findAllByStatus(Status.PENDING, pageRequest);
        return dentistList.map(dentist -> modelMapper.map(dentist,DentistListResponse.class));
    }

    @Override
    @Transactional
    public DentistDetailResponse approveDentistAccount(Long id, boolean isApproved) {
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", id));
        if (!dentist.getStatus().equals(Status.PENDING))
            throw new ApiException(HttpStatus.CONFLICT, "Dentist status must be pending to be approved");
        if(isApproved) {
            dentist.setStatus(Status.ACTIVE);
            String randomPassword = AutomaticGeneratedPassword.generateRandomPassword();
            dentist = dentistRepository.save(dentist);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
            message.setTo(dentist.getEmail());
            // Set a meaningful message
            message.setSubject("[F-Dental] - Tài khoản được duyệt và tạo thành công");
            message.setText("Hi, " + dentist.getFullName() + ",\n\n" +
                    "Tài khoản đăng nhập vào hệ thống F-Dental của bạn đã được duyệt và tạo thành công.\n" +
                    "Vui lòng truy cập hệ thống theo thông tin sau:\n" +
                    "• Username: " + dentist.getUsername() + "\n" +
                    "• Password: " + randomPassword + "\n" + // Placeholder for the password
                    "Lưu ý: Vui lòng thay đổi mật khẩu sau khi đăng nhập.\n");
            // Send the email (assuming you have a mailSender bean configured)
            mailSender.send(message);
            return modelMapper.map(dentist, DentistDetailResponse.class);
        }else {
            dentist.setStatus(Status.DENIED);
            dentistRepository.delete(dentist);
            return modelMapper.map(dentist, DentistDetailResponse.class);
        }
    }


    @Override
    public DentistDetailResponse getDentistDetail(Long id) {
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", id));
        return modelMapper.map(dentist, DentistDetailResponse.class);
    }

    @Override
    @Transactional
    public DentistDetailResponse createDentist(DentistCreateRequest request) {
        if(authenticateService.isUsernameOrEmailExisted(request.getUsername(), request.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username/Email is already used");
        if(request.getDob().isAfter(LocalDate.now()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dob cannot be after present date!");
        ClinicBranch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic Branch", "id", request.getBranchId()));
        Dentist dentist = new Dentist();
        modelMapper.map(request, dentist);
        dentist.setStatus(Status.PENDING);
        dentist.setClinicBranch(branch);
        dentist = dentistRepository.save(dentist);
        return modelMapper.map(dentist, DentistDetailResponse.class);
    }

    @Override
    @Transactional
    public DentistDetailResponse updateDentistDetails(DentistUpdateRequest request) {
        Dentist existingDentist = dentistRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", request.getId()));
        if(!existingDentist.getUsername().equals(request.getUsername()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username cannot be changed!");
        if(!existingDentist.getEmail().equals(request.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email cannot be changed!");
        if(request.getDob().isAfter(LocalDate.now()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dob cannot be after present date!");
        Status.isValid(request.getStatus());
        if(existingDentist.getStatus().equals(Status.PENDING) || existingDentist.getStatus().equals(Status.DENIED))
            throw new ApiException(HttpStatus.CONFLICT, "Status cannot be changed");
        ClinicBranch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic Branch", "id", request.getBranchId()));
        modelMapper.map(request, existingDentist);
        existingDentist.setClinicBranch(branch);
        Dentist updatedDentist = dentistRepository.save(existingDentist);
        return modelMapper.map(updatedDentist, DentistDetailResponse.class);
    }

    @Override
    @Transactional
    public DentistDetailResponse removeDentist(Long id) {
        Dentist existingDentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", id));
        if(existingDentist.getStatus().equals(Status.ACTIVE)) {
            existingDentist.setStatus(Status.INACTIVE);
        }else throw new ApiException(HttpStatus.CONFLICT, "Account is not able to remove because it may in INACTIVE or PENDING status");
        Dentist updatedDentist = dentistRepository.save(existingDentist);
        return modelMapper.map(updatedDentist, DentistDetailResponse.class);
    }
}
