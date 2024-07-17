package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.ClinicRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ClinicUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.WorkingHoursCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.*;
import com.example.dentalclinicschedulingplatform.utils.AutomaticGeneratedPassword;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicService implements IClinicService {

    private final ModelMapper modelMapper;
    private final ClinicRepository clinicRepository;
    private final OwnerRepository ownerRepository;
    private final ClinicBranchRepository branchRepository;
    private final ServiceRepository serviceRepository;
    private final DentistRepository dentistRepository;
    private final BlogRepository blogRepository;
    private final IMailService mailService;
    private final IBranchService branchService;
    private final IOwnerService ownerService;
    private final IFeedbackService feedbackService;
    private final IWorkingHoursService workingHoursService;
    private final IAuthenticateService authenticateService;

    @Override
    @Transactional
    public ClinicRegisterResponse registerClinic(ClinicRegisterRequest request) {
        Clinic clinic = new Clinic();
        clinic.setClinicName(request.getClinicName());
        clinic.setAddress(request.getAddress());
        clinic.setCity(request.getCity());
        clinic.setPhone(request.getClinicPhone());
        clinic.setClinicRegistration(request.getClinicRegistration());
        clinic.setWebsiteUrl(request.getWebsiteUrl());
        clinic.setClinicImage(request.getClinicImage());
        clinic.setStatus(ClinicStatus.PENDING);
        ClinicOwner tmpOwner = ownerService.registerOwnerFromRequest(request.getOwnerInformation());
        clinic.setClinicOwner(tmpOwner);
        Clinic tmpClinic = clinicRepository.save(clinic);
        mailService.sendClinicRequestConfirmationMail(tmpOwner.getFullName(), tmpOwner.getEmail());
        return getClinicRegisterResponse(tmpClinic, tmpOwner);
    }

    @Override
    public ApprovedClinicResponse approveClinic(Long clinicId, boolean isApproved) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", clinicId));
        if(isApproved) {
            clinic.setStatus(ClinicStatus.APPROVED);
            Clinic savedClinic = clinicRepository.save(clinic);
            ClinicBranch approvedBranch = branchService.createMainBranch(savedClinic);
            if (savedClinic.getClinicOwner() == null)
                throw new ApiException(HttpStatus.CONFLICT, "This clinic does not have its owner");
            String randomPassword = AutomaticGeneratedPassword.generateRandomPassword();
            ClinicOwner approvedOwner = ownerService.approveOwnerAccount(savedClinic.getClinicOwner().getId(), randomPassword);
            mailService.sendClinicRequestApprovalMail(approvedOwner, randomPassword);

            ApprovedOwnerResponse ownerResponse = modelMapper.map(approvedOwner, ApprovedOwnerResponse.class);
            ApprovedBranchResponse branchResponse = modelMapper.map(approvedBranch, ApprovedBranchResponse.class);
            ApprovedClinicResponse clinicResponse = modelMapper.map(savedClinic, ApprovedClinicResponse.class);
            clinicResponse.setOwnerDetail(ownerResponse);
            clinicResponse.setBranchDetail(branchResponse);
            return clinicResponse;
        }else {
            clinic.setStatus(ClinicStatus.DENIED);
            mailService.sendClinicRequestRejectionMail(clinic.getClinicOwner().getFullName(), clinic.getClinicOwner().getEmail());
            clinicRepository.delete(clinic);
            return modelMapper.map(clinic, ApprovedClinicResponse.class);
        }
    }

    @Override
    public ClinicDetailResponse viewClinicDetail(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", clinicId));
        ClinicDetailResponse response = modelMapper.map(clinic, ClinicDetailResponse.class);
        response.setOwnerName(clinic.getClinicOwner().getFullName());
        return response;
    }

    @Override
    @Transactional
    public ClinicUpdateResponse updateClinicInformation(ClinicUpdateRequest request) {
        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", request.getClinicId()));
        if(clinic.getStatus().equals(ClinicStatus.INACTIVE) || clinic.getStatus().equals(ClinicStatus.PENDING))
            throw new ApiException(HttpStatus.CONFLICT, "You cannot update clinic information because this clinic is inactive or pending!");
        if (clinic.getEmail() != null && !clinic.getEmail().equals(request.getEmail())){
            if(clinicRepository.existsByEmail(request.getEmail()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "This email is already registered by another clinic!");
        }
        if (!clinic.getPhone().equals(request.getPhone())){
            if(clinicRepository.existsByPhone(request.getPhone()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "This phone is already registered by another clinic!");
        }
        if (!clinic.getWebsiteUrl().equals(request.getWebsiteUrl())) {
            if(clinicRepository.existsByWebsiteUrl(request.getWebsiteUrl()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "This website is already registered by another clinic!");
        }
        clinic.setClinicName(request.getClinicName());
        clinic.setAddress(request.getAddress());
        clinic.setEmail(request.getEmail());
        clinic.setCity(request.getCity());
        clinic.setPhone(request.getPhone());
        clinic.setDescription(request.getDescription());
        clinic.setLogo(request.getLogo());
        clinic.setWebsiteUrl(request.getWebsiteUrl());
        clinic.setClinicImage(request.getClinicImage());
        clinic.setStatus(ClinicStatus.ACTIVE);
        branchService.updateMainBranch(request);
        Clinic updatedClinic = clinicRepository.save(clinic);
        ClinicUpdateResponse res = modelMapper.map(updatedClinic, ClinicUpdateResponse.class);

        List<WorkingHoursResponse> clinicWorkingHours = workingHoursService.viewWorkingHour(clinic.getClinicId());

        if(clinic.getStatus().equals(ClinicStatus.APPROVED) || clinic.getStatus().equals(ClinicStatus.ACTIVE)){
            if(request.getWorkingHours().isEmpty())
                throw new ApiException(HttpStatus.NOT_FOUND, "Working hours must not be empty");

            List<WorkingHoursCreateRequest> workingHoursToCreate = new ArrayList<>();
            List<WorkingHoursCreateRequest> workingHoursToUpdate = new ArrayList<>();

            for (WorkingHoursCreateRequest reqWorkingHour: request.getWorkingHours()) {
                Optional<WorkingHoursResponse> existingWorkingHours = clinicWorkingHours.stream()
                        .filter(clinicWorkingHour -> clinicWorkingHour.getDay().equals(reqWorkingHour.getDay())).findFirst();

                if (existingWorkingHours.isPresent()) {
                    workingHoursToUpdate.add(reqWorkingHour);
                }else {
                    workingHoursToCreate.add(reqWorkingHour);
                }
            }

            if (!workingHoursToCreate.isEmpty()) {
                workingHoursService.createWorkingHour(workingHoursToCreate);
            }

            if (!workingHoursToUpdate.isEmpty()) {
                workingHoursService.updateWorkingHour(workingHoursToUpdate);
            }
        }

        List<WorkingHoursResponse> newClinicWorkingHours = workingHoursService.viewWorkingHour(clinic.getClinicId());
        res.setWorkingHours(newClinicWorkingHours);
        return res;
    }

    @Override
    public Page<ClinicListResponse> getAllActiveClinic(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<Clinic> clinics;
        clinics = clinicRepository.findAllByStatus(ClinicStatus.ACTIVE, pageRequest);
        return clinics.map(this::mapListRes);
    }

    @Override
    public Page<ClinicListResponse> getAllClinic(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<Clinic> clinics;
        clinics = clinicRepository.findAll(pageRequest);
        return clinics.map(this::mapListRes);
    }

    @Override
    public Page<PendingClinicListResponse> getClinicPendingList(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<Clinic> clinics;
        clinics = clinicRepository.findAllByStatus(ClinicStatus.PENDING, pageRequest);
        return clinics.map(clinic -> new PendingClinicListResponse(clinic.getClinicId(),
                clinic.getClinicName(), clinic.getClinicOwner().getFullName()));
    }

    @Override
    public ClinicRegisterResponse getPendingClinicDetail(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", clinicId));
        return getClinicRegisterResponse(clinic, clinic.getClinicOwner());
    }

    @Override
    public ClinicDetailResponse reactivateClinic(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
            .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", clinicId));

        if(!clinic.getStatus().equals(ClinicStatus.INACTIVE))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot re-activate clinic because clinic status is not INACTIVE status");
        clinic.setStatus(ClinicStatus.ACTIVE);
        clinic = clinicRepository.save(clinic);
        ClinicDetailResponse response = modelMapper.map(clinic, ClinicDetailResponse.class);
        response.setOwnerName(clinic.getClinicOwner().getFullName());
        return response;
    }

    @Override
    public ClinicDetailResponse deactivateClinic(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", clinicId));

        if(!clinic.getStatus().equals(ClinicStatus.ACTIVE))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot deactivate clinic because clinic status is not ACTIVE status");
        clinic.setStatus(ClinicStatus.INACTIVE);
        clinic = clinicRepository.save(clinic);
        ClinicDetailResponse response = modelMapper.map(clinic, ClinicDetailResponse.class);
        response.setOwnerName(clinic.getClinicOwner().getFullName());
        return response;
    }

    private ClinicRegisterResponse getClinicRegisterResponse(Clinic tmpClinic, ClinicOwner tmpOwner) {
        ClinicRegisterResponse response = new ClinicRegisterResponse();
        response.setClinicId(tmpClinic.getClinicId());
        response.setClinicName(tmpClinic.getClinicName());
        response.setAddress(tmpClinic.getAddress());
        response.setCity(tmpClinic.getCity());
        response.setPhone(tmpClinic.getPhone());
        response.setClinicRegistration(tmpClinic.getClinicRegistration());
        response.setWebsiteUrl(tmpClinic.getWebsiteUrl());
        response.setClinicImage(tmpClinic.getClinicImage());
        response.setClinicStatus(tmpClinic.getStatus());
        response.setOwnerDetail(modelMapper.map(tmpOwner, OwnerRegisterResponse.class));
        return response;
    }

    private ClinicListResponse mapListRes(Clinic clinic){
        ClinicListResponse res = modelMapper.map(clinic, ClinicListResponse.class);
        res.setOwnerName(clinic.getClinicOwner().getFullName());
        res.setFeedbackCount(feedbackService.getFeedbackByClinicId(clinic.getClinicId()).getFeedbacks ( ).size ());
        return res;
    }

    @Override
    public ClinicStaffAndDentistResponse getAllStaffAndDentistByOwner() {
        try {
            ClinicOwner clinicOwner = ownerRepository.findByUsername(authenticateService.getUserInfo().getUsername())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic owner not found"));

            Clinic clinic = clinicRepository.findByClinicOwnerId(clinicOwner.getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

            List<ClinicBranch> clinicBranchList = branchRepository.findAllByClinic_ClinicId(clinic.getClinicId());

            // Lấy danh sách staff từ clinicBranchList
            List<ClinicStaff> staffList = clinicBranchList.stream()
                    .flatMap(branch -> branch.getStaffs().stream())
                    .collect(Collectors.toList());

            // Chuyển đổi danh sách ClinicStaff sang StaffSummaryResponse
            List<StaffSummaryResponse> staffSummaryResponses = staffList.stream()
                    .map(StaffSummaryResponse::new)
                    .collect(Collectors.toList());

            // Lấy danh sách dentist từ clinicBranchList
            List<Dentist> dentistList = clinicBranchList.stream()
                    .flatMap(branch -> branch.getDentists().stream())
                    .collect(Collectors.toList());

            // Chuyển đổi danh sách Dentist sang DentistListResponse
            List<DentistListResponse> dentistListResponses = dentistList.stream()
                    .map(dentist -> new DentistListResponse(
                            dentist.getId(),
                            dentist.getAvatar(),
                            dentist.getFullName(),
                            dentist.getUsername(),
                            dentist.getSpecialty(),
                            dentist.getClinicBranch().getCity(),
                            dentist.getClinicBranch().getBranchId(),
                            dentist.getClinicBranch().getBranchName(),
                            dentist.getClinicBranch().getClinic().getClinicName(),
                            dentist.getStatus()))
                    .collect(Collectors.toList());

            return new ClinicStaffAndDentistResponse(staffSummaryResponses, dentistListResponses);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Map<String, Object> search(int page, int size, String searchValue, String filter) {

        Pageable pageable = PageRequest.of(page, size);

        Map<String, Object> searchResult = new HashMap<>();

        List<DentistListResponse> dentistViewListResponses = new ArrayList<>();
        List<ServiceViewListResponse> serviceViewListResponses = new ArrayList<>();
        List<ClinicListResponse> clinicListResponses = new ArrayList<>();
        List<BlogListResponse> blogListResponses = new ArrayList<>();


        switch (filter){
            case "DentalClinic":
                Page<Clinic> searchDentalClinic = clinicRepository.findClinic(searchValue, pageable);
                for (Clinic clinic : searchDentalClinic) {
                    ClinicListResponse clinicListResponse = modelMapper.map(clinic, ClinicListResponse.class);
                    clinicListResponse.setFeedbackCount(feedbackService.getFeedbackByClinicId(clinic.getClinicId()).getFeedbacks().size());
                    clinicListResponses.add(clinicListResponse);
                }
                searchResult.put("Searched clinic", clinicListResponses);
                break;
            case "Dentist":
                Page<Dentist> searchDentist = dentistRepository.findDentist(searchValue, pageable);
                for (Dentist dentist : searchDentist) {
                    dentistViewListResponses.add(modelMapper.map(dentist, DentistListResponse.class));
                }
                searchResult.put("Searched Dentist", dentistViewListResponses);
                break;
            case "Service":
                Page<com.example.dentalclinicschedulingplatform.entity.Service> searchServices = serviceRepository.findServices(searchValue, pageable);
                for (com.example.dentalclinicschedulingplatform.entity.Service service: searchServices) {
                    serviceViewListResponses.add(modelMapper.map(service, ServiceViewListResponse.class));
                }
                searchResult.put("Searched service", serviceViewListResponses);
                break;
            case "Blog":
                Page<Blog> searchBlog = blogRepository.findBlogs(searchValue, pageable);
                for (Blog blog : searchBlog) {
                    blogListResponses.add(modelMapper.map(blog, BlogListResponse.class));
                }
                searchResult.put("Searched Blog", blogListResponses);
                break;
            default:
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid filter");
        }

        return searchResult;
    }
}
