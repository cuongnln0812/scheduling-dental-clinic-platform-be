package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.IAppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService implements IAppointmentService {

    private final CustomerRepository customerRepository;

    private final AppointmentRepository appointmentRepository;

    private final ClinicBranchRepository clinicBranchRepository;

    private final ServiceRepository serviceRepository;

    private final SlotRepository slotRepository;

    private final AuthenticateService authenticateService;

    private final DentistRepository dentistRepository;

    private final ClinicRepository clinicRepository;

    private final StaffRepository staffRepository;

    private final ModelMapper modelMapper;
    @Override
    public Map<String, Object> getCustomerAppointments(UserInformationRes userInformationRes, int page, int size) {

        Map<String, Object> customerAppointments = new HashMap<>();

        Pageable pageable = PageRequest.of(page, size);

        Customer customer = customerRepository.findByUsername(userInformationRes.getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));

        Page<Appointment> appointments = appointmentRepository.findAppointmentsByCustomerId(customer.getId(), pageable);

        List<AppointmentViewDetailsResponse> pendingAppointments = new ArrayList<>();

        List<AppointmentViewListResponse> doneAppointments = new ArrayList<>();

        for (Appointment appointment: appointments) {
            if (appointment.getStatus().equals(AppointmentStatus.PENDING)) {

                Appointment currAppointment = appointmentRepository.findById(appointment.getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

                Customer currCustomer = customerRepository.findByUsername(currAppointment.getCustomer().getUsername())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));

                ClinicBranch currBranch = clinicBranchRepository.findById(currAppointment.getClinicBranch().getBranchId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

                com.example.dentalclinicschedulingplatform.entity.Service currService = serviceRepository.findById(currAppointment.getService().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

                Slot currSlot = slotRepository.findById(currAppointment.getSlot().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

                Dentist currDentist = dentistRepository.findById(currAppointment.getDentist().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dentist not found"));

                pendingAppointments.add(new AppointmentViewDetailsResponse(currAppointment.getId(), currAppointment.getCustomerName(), currAppointment.getCustomerAddress(), currAppointment.getCustomerPhone(),
                        currAppointment.getCustomerAge(), currAppointment.getCustomerEmail(), currAppointment.getAppointmentDate()
                        , currService.getDuration(), modelMapper.map(currSlot, SlotDetailsResponse.class), modelMapper.map(currBranch, BranchSummaryResponse.class ), modelMapper.map(currDentist, DentistViewListResponse.class)
                        , modelMapper.map(currService, ServiceViewListResponse.class)));

            }else if (appointment.getStatus().equals(AppointmentStatus.DONE)){
                doneAppointments.add(modelMapper.map(appointment, AppointmentViewListResponse.class));
            }
        }

        customerAppointments.put("Current Appointment", pendingAppointments);
        customerAppointments.put("Appointment History", doneAppointments);

        return customerAppointments;
    }

    @Override
    public Map<String, Object> getAppointments(UserInformationRes userInformationRes, int page, int size) {

        List<AppointmentViewDetailsResponse> pendingAppointments = new ArrayList<>();

        List<AppointmentViewListResponse> doneAppointments = new ArrayList<>();

        if (!userInformationRes.getRole().equals(UserType.STAFF.toString()) &&
                !userInformationRes.getRole().equals(UserType.DENTIST.toString())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

        Map<String, Object> appointmentsList = new HashMap<>();

        Pageable pageable = PageRequest.of(page, size);

        ClinicStaff staff = staffRepository.findByUsername(userInformationRes.getUsername())
                .orElse(null);

        if (staff == null) {
            Dentist dentist = dentistRepository.findByUsername(userInformationRes.getUsername())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

            Page<Appointment> appointments = appointmentRepository.findAppointmentsByDentistId(dentist.getId(), pageable);

            for (Appointment appointment: appointments) {
                if (appointment.getStatus().equals(AppointmentStatus.PENDING)){

                    Appointment currAppointment = appointmentRepository.findById(appointment.getId())
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

                    Customer currCustomer = customerRepository.findByUsername(appointment.getCustomer().getUsername())
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));

                    ClinicBranch currBranch = clinicBranchRepository.findById(currAppointment.getClinicBranch().getBranchId())
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

                    com.example.dentalclinicschedulingplatform.entity.Service currService = serviceRepository.findById(currAppointment.getService().getId())
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

                    Slot currSlot = slotRepository.findById(currAppointment.getSlot().getId())
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

                    Dentist currDentist = dentistRepository.findById(currAppointment.getDentist().getId())
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dentist not found"));

                    pendingAppointments.add(new AppointmentViewDetailsResponse(currAppointment.getId(), currAppointment.getCustomerName(), currAppointment.getCustomerAddress(), currAppointment.getCustomerPhone(),
                            currAppointment.getCustomerAge(), currAppointment.getCustomerEmail(), currAppointment.getAppointmentDate()
                            , currService.getDuration(), modelMapper.map(currSlot, SlotDetailsResponse.class), modelMapper.map(currBranch, BranchSummaryResponse.class ), modelMapper.map(currDentist, DentistViewListResponse.class)
                            , modelMapper.map(currService, ServiceViewListResponse.class)));

                }else if (appointment.getStatus().equals(AppointmentStatus.DONE)) {
                    doneAppointments.add(modelMapper.map(appointment, AppointmentViewListResponse.class));
                }
            }

            appointmentsList.put("Current appointment of dentist", pendingAppointments);
            appointmentsList.put("Appointment history of dentist", doneAppointments);

            return appointmentsList;
        }

        ClinicBranch clinicBranch = clinicBranchRepository.findById(staff.getClinicBranch().getBranchId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

        Page<Appointment> appointments = appointmentRepository.findAppointmentsByClinicBranch_BranchId(clinicBranch.getBranchId(), pageable);

        for (Appointment appointment: appointments) {
            if (appointment.getStatus().equals(AppointmentStatus.PENDING)){

                Appointment currAppointment = appointmentRepository.findById(appointment.getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

                Customer currCustomer = customerRepository.findByUsername(appointment.getCustomer().getUsername())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));

                ClinicBranch currBranch = clinicBranchRepository.findById(currAppointment.getClinicBranch().getBranchId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

                com.example.dentalclinicschedulingplatform.entity.Service currService = serviceRepository.findById(currAppointment.getService().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

                Slot currSlot = slotRepository.findById(currAppointment.getSlot().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

                Dentist currDentist = dentistRepository.findById(currAppointment.getDentist().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dentist not found"));

                pendingAppointments.add(new AppointmentViewDetailsResponse(currAppointment.getId(), currAppointment.getCustomerName(), currAppointment.getCustomerAddress(), currAppointment.getCustomerPhone(),
                        currAppointment.getCustomerAge(), currAppointment.getCustomerEmail(), currAppointment.getAppointmentDate()
                        , currService.getDuration(), modelMapper.map(currSlot, SlotDetailsResponse.class), modelMapper.map(currBranch, BranchSummaryResponse.class ), modelMapper.map(currDentist, DentistViewListResponse.class)
                        , modelMapper.map(currService, ServiceViewListResponse.class)));
            }else if (appointment.getStatus().equals(AppointmentStatus.DONE)) {
                doneAppointments.add(modelMapper.map(appointment, AppointmentViewListResponse.class));
            }
        }

        appointmentsList.put("Current appointments of clinic branch", pendingAppointments);
        appointmentsList.put("Appointments history of clinic branch", doneAppointments);
        return appointmentsList;
    }

    @Override
    public Appointment makeAppointment(Appointment appointment) {
        return null;
    }

    @Override
    public AppointmentViewDetailsResponse viewDetailsAppointment(Long appointmentId) {

        Customer customer = customerRepository.findByUsername(authenticateService.getUserInfo().getUsername())
                .orElse(null);

        Appointment currAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Customer currCustomer = customerRepository.findByUsername(currAppointment.getCustomer().getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));


        ClinicBranch currBranch = clinicBranchRepository.findById(currAppointment.getClinicBranch().getBranchId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

        com.example.dentalclinicschedulingplatform.entity.Service currService = serviceRepository.findById(currAppointment.getService().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

        Slot currSlot = slotRepository.findById(currAppointment.getSlot().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

        Dentist currDentist = dentistRepository.findById(currAppointment.getDentist().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dentist not found"));

        if (customer != null) {
            if (!customer.getId().equals(currCustomer.getId())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Can not view another customers' appointments");
            }
        }
        
        return new AppointmentViewDetailsResponse(currAppointment.getId(), currAppointment.getCustomerName(), currAppointment.getCustomerAddress(), currAppointment.getCustomerPhone(),
                currAppointment.getCustomerAge(), currAppointment.getCustomerEmail(), currAppointment.getAppointmentDate()
                , currService.getDuration(), modelMapper.map(currSlot, SlotDetailsResponse.class), modelMapper.map(currBranch, BranchSummaryResponse.class ), modelMapper.map(currDentist, DentistViewListResponse.class)
                , modelMapper.map(currService, ServiceViewListResponse.class));
    }
}
