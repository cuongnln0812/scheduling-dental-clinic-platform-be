package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.response.AccountListResponse;
import com.example.dentalclinicschedulingplatform.payload.response.AdminDashboardResponse;
import com.example.dentalclinicschedulingplatform.payload.response.OwnerDashboardResponse;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.IAccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;
    private final DentistRepository dentistRepository;
    private final StaffRepository staffRepository;
    private final OwnerRepository ownerRepository;
    private final SystemAdminRepository adminRepository;
    private final AppointmentRepository appointmentRepository;
    private final FeedbackRepository feedbackRepository;
    private final ClinicRepository clinicRepository;
    private final BlogRepository blogRepository;

    @Override
    public Page<AccountListResponse> getAllAccount(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Customer> customerPage = customerRepository.findAll(pageable);
        Page<AccountListResponse> customerResponses = customerPage.map(user -> {
                AccountListResponse res = modelMapper.map(user, AccountListResponse.class);
                res.setRoleName("CUSTOMER");
                res.setStatus(user.isStatus() ? "ACTIVE" : "INACTIVE");
                return res;
        });

        Page<Dentist> dentistPage = dentistRepository.findAllActiveAndInactive(pageable);
        Page<AccountListResponse> dentistResponses = dentistPage.map(user -> {
                AccountListResponse res = modelMapper.map(user, AccountListResponse.class);
                res.setRoleName("DENTIST");
                res.setStatus(user.getStatus().toString());
                return res;
        });

        Page<ClinicStaff> staffPage = staffRepository.findAllActiveAndInactive(pageable);
        Page<AccountListResponse> staffResponse = staffPage.map(user -> {
                AccountListResponse res = modelMapper.map(user, AccountListResponse.class);
                res.setRoleName("STAFF");
                res.setStatus(user.getStatus().toString());
                return res;
        });

        Page<ClinicOwner> ownerPage = ownerRepository.findAllActiveAndInactive(pageable);
        Page<AccountListResponse> ownerResponses = ownerPage.map(user -> {
            AccountListResponse res = modelMapper.map(user, AccountListResponse.class);
            res.setRoleName("OWNER");
            res.setStatus(user.getStatus().toString());
            return res;
        });

        Page<SystemAdmin> adminPage = adminRepository.findAll(pageable);
        Page<AccountListResponse> adminResponse = adminPage.map(user -> {
            AccountListResponse res = modelMapper.map(user, AccountListResponse.class);
            res.setId(user.getAdminId());
            res.setRoleName("ADMIN");
            res.setStatus(user.isStatus() ? "ACTIVE" : "INACTIVE");
            return res;
        });

        return PageableExecutionUtils.getPage(
                Stream.of(customerResponses, dentistResponses, staffResponse, ownerResponses, adminResponse)
                        .flatMap(pageContent -> pageContent.getContent().stream())
                        .collect(Collectors.toList()),
                pageable,
                () -> customerResponses.getTotalElements() +
                        dentistResponses.getTotalElements() +
                        staffResponse.getTotalElements() +
                        ownerResponses.getTotalElements() +
                        adminResponse.getTotalElements()
        );
    }

    @Override
    public OwnerDashboardResponse getOwnerDashboardStatistics() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ClinicOwner owner = ownerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "username", username));
        List<Appointment> appointments = appointmentRepository.findAllByOwnerUsername(owner.getUsername());
        Long numOfAppointments = (long) appointments.size();
        Long numOfDent = dentistRepository.countAllByOwnerUsername(owner.getUsername());
        Long numOfStaff = staffRepository.countAllByOwnerUsername(owner.getUsername());
        Long numOfFeedbacks = feedbackRepository.countAllByOwnerUsername(owner.getUsername());
        OwnerDashboardResponse response = new OwnerDashboardResponse();
        response.setNumberOfAppointments(numOfAppointments);
        response.setNumberOfPendingAppointments(appointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.PENDING).count());
        response.setNumberOfCanceledAppointments(appointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.CANCELED).count());
        response.setNumberOfDoneAppointments(appointments.stream()
                .filter(appointment -> appointment.getStatus() == AppointmentStatus.DONE).count());
        response.setNumberOfClinicUsers(numOfStaff + numOfDent);
        response.setNumberOfClinicDentists(numOfDent);
        response.setNumberOfClinicStaffs(numOfStaff);
        response.setNumberOfClinicFeedbacks(numOfFeedbacks);
        return response;
    }

    @Override
    public AdminDashboardResponse getAdminDashboardStatistics() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SystemAdmin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("System Admin", "username", username));
        List<Clinic> clinics = clinicRepository.findAll();
        Long numOfCus = customerRepository.count();
        Long numOfDent = dentistRepository.count();
        Long numOfStaff = staffRepository.count();
        Long numOfOwner = ownerRepository.count();
        Long numOfClinic = (long) clinics.size();
        Long numOfBlogs = blogRepository.count();
        AdminDashboardResponse response = new AdminDashboardResponse();
        response.setNumberOfDentalClinic(numOfClinic);
        response.setNumberOfActiveDentalClinic(clinics.stream()
                .filter(clinic -> clinic.getStatus() == ClinicStatus.ACTIVE).count());
        response.setNumberOfInactiveDentalClinic(clinics.stream()
                .filter(clinic -> clinic.getStatus() == ClinicStatus.INACTIVE).count());
        response.setNumberOfPendingDentalClinic(clinics.stream()
                .filter(clinic -> clinic.getStatus() == ClinicStatus.PENDING).count());
        response.setNumberOfClinicDentists(numOfDent);
        response.setNumberOfClinicStaffs(numOfStaff);
        response.setNumberOfCustomers(numOfCus);
        response.setNumberOfClinicOwners(numOfOwner);
        response.setNumberOfBlogs(numOfBlogs);
        response.setNumberOfClinicUsers(numOfCus+numOfDent+numOfOwner+numOfStaff);
        return response;
    }
}
