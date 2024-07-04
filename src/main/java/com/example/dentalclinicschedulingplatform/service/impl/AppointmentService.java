package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.AppointmentCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.IAppointmentService;
import com.example.dentalclinicschedulingplatform.service.IMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private final SlotService slotService;

    private final DentistService dentistService;

    private final IMailService mailService;

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

                Customer currCustomer = customerRepository.findById(currAppointment.getCustomer().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));

                ClinicBranch currBranch = clinicBranchRepository.findById(currAppointment.getClinicBranch().getBranchId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

                com.example.dentalclinicschedulingplatform.entity.Service currService = serviceRepository.findById(currAppointment.getService().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

                Slot currSlot = slotRepository.findById(currAppointment.getSlot().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

                Dentist currDentist = dentistRepository.findById(currAppointment.getDentist().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dentist not found"));

                pendingAppointments.add(new AppointmentViewDetailsResponse(currAppointment.getId(), currAppointment.getStatus().name(), currCustomer.getId(),currAppointment.getCustomerName(), currAppointment.getCustomerAddress(), currAppointment.getCustomerPhone(),
                         currAppointment.getCustomerDob(), currAppointment.getCustomerAge(), currAppointment.getCustomerEmail(), currAppointment.getAppointmentDate()
                        , currService.getDuration(), modelMapper.map(currSlot, SlotDetailsResponse.class), modelMapper.map(currBranch, BranchSummaryResponse.class ), modelMapper.map(currDentist, DentistViewListResponse.class)
                        , modelMapper.map(currService, ServiceViewListResponse.class), currAppointment.getCreatedDate()));

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

                    pendingAppointments.add(new AppointmentViewDetailsResponse(currAppointment.getId(), currAppointment.getStatus().name() , currCustomer.getId(),currAppointment.getCustomerName(), currAppointment.getCustomerAddress(), currAppointment.getCustomerPhone(),
                            currAppointment.getCustomerDob() ,currAppointment.getCustomerAge(), currAppointment.getCustomerEmail(), currAppointment.getAppointmentDate()
                            , currService.getDuration(), modelMapper.map(currSlot, SlotDetailsResponse.class), modelMapper.map(currBranch, BranchSummaryResponse.class ), modelMapper.map(currDentist, DentistViewListResponse.class)
                            , modelMapper.map(currService, ServiceViewListResponse.class), currAppointment.getCreatedDate()));

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

                Customer currCustomer = customerRepository.findById(appointment.getCustomer().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));

                ClinicBranch currBranch = clinicBranchRepository.findById(currAppointment.getClinicBranch().getBranchId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch not found"));

                com.example.dentalclinicschedulingplatform.entity.Service currService = serviceRepository.findById(currAppointment.getService().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

                Slot currSlot = slotRepository.findById(currAppointment.getSlot().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

                Dentist currDentist = dentistRepository.findById(currAppointment.getDentist().getId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dentist not found"));

                pendingAppointments.add(new AppointmentViewDetailsResponse(currAppointment.getId(), currAppointment.getStatus().name(), currCustomer.getId(),currAppointment.getCustomerName(), currAppointment.getCustomerAddress(), currAppointment.getCustomerPhone(),
                        currAppointment.getCustomerDob(), currAppointment.getCustomerAge(), currAppointment.getCustomerEmail(), currAppointment.getAppointmentDate()
                        , currService.getDuration(), modelMapper.map(currSlot, SlotDetailsResponse.class), modelMapper.map(currBranch, BranchSummaryResponse.class ), modelMapper.map(currDentist, DentistViewListResponse.class)
                        , modelMapper.map(currService, ServiceViewListResponse.class), currAppointment.getCreatedDate()));
            }else if (appointment.getStatus().equals(AppointmentStatus.DONE)) {
                doneAppointments.add(modelMapper.map(appointment, AppointmentViewListResponse.class));
            }
        }

        appointmentsList.put("Current appointments of clinic branch", pendingAppointments);
        appointmentsList.put("Appointments history of clinic branch", doneAppointments);
        return appointmentsList;
    }

    @Override
    public AppointmentViewDetailsResponse makeAppointment(AppointmentCreateRequest appointment) {

        List<Dentist> dentistList = new ArrayList<>();
        List<Slot> slotList = new ArrayList<>();

        if (!authenticateService.getUserInfo().getRole().equals(UserType.CUSTOMER.toString()) &&
                !authenticateService.getUserInfo().getRole().equals(UserType.STAFF.toString())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid date");
        }

        Customer customer = customerRepository.findByUsername(authenticateService.getUserInfo().getUsername())
                .orElse(null);

        if (customer == null && appointment.getCustomerId() != null) {
            customer = customerRepository.findById(appointment.getCustomerId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));
        }

        ClinicStaff staff = staffRepository.findByUsername(authenticateService.getUserInfo().getUsername())
                .orElse(null);

        WorkingHoursDetailsResponse workingHoursDetailsResponse = slotService.viewAvailableSlotsByDateByClinicBranch(appointment.getAppointmentDate(), appointment.getClinicBranchId());

        List<SlotDetailsResponse> slotDetailsResponses = workingHoursDetailsResponse.slots;

        List<DentistViewListResponse> availableDentists =
                dentistService.getAvailableDentistOfDateByBranch(appointment.getClinicBranchId(), appointment.getAppointmentDate(), appointment.getSlotId());

        for (DentistViewListResponse dentistItem: availableDentists) {
            Dentist dentist = dentistRepository.findById(dentistItem.getDentistId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dentist not found"));
            dentistList.add(dentist);
        }

        for (SlotDetailsResponse slotItem: slotDetailsResponses) {
            Slot slot = slotRepository.findById(slotItem.getSlotId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));
            slotList.add(slot);
        }

        Slot currSlot = slotRepository.findById((appointment.getSlotId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Slot not found"));

        if (!slotList.contains(currSlot)){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current slot is occupied");
        }

        ClinicBranch currClinicBranch = clinicBranchRepository.findById(appointment.getClinicBranchId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ClinicBranch not found"));

        Clinic currClinic = clinicRepository.findById(currClinicBranch.getClinic().getClinicId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        if (staff != null) {
            List<ClinicStaff> staffOfBranch = staffRepository.findAllByClinicBranch_BranchId(currClinicBranch.getBranchId());
            if (!staffOfBranch.contains(staff)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Staff does not belong to current branch");
            }
        }

        List<com.example.dentalclinicschedulingplatform.entity.Service> serviceOfClinic = serviceRepository.findServicesByClinic_ClinicId(currClinic.getClinicId());

        List<Dentist> dentistOfClinicBranch = dentistRepository.findAllByClinicBranch_BranchId(currClinicBranch.getBranchId());

        Dentist currDentist = dentistRepository.findById(appointment.getDentistId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dentist not found"));

        com.example.dentalclinicschedulingplatform.entity.Service currService = serviceRepository.findById(appointment.getServiceId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service not found"));

        if (!dentistOfClinicBranch.contains(currDentist)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dentist doest not belong to current branch");
        }

        if (!serviceOfClinic.contains(currService)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Service does not belong to current clinic");
        }

        if (!dentistList.contains(currDentist)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current dentist is occupied");
        }

        Appointment newAppointment = new Appointment();
        newAppointment.setCustomerName(appointment.customerName);
        newAppointment.setCustomerAddress(appointment.getCustomerAddress());
        newAppointment.setCustomerPhone(appointment.getCustomerPhone());
        newAppointment.setCustomerDob(appointment.getCustomerDob());
        newAppointment.setCustomerAge(LocalDate.now().getYear() - appointment.getCustomerDob().getYear());
        newAppointment.setCustomerEmail(appointment.getCustomerEmail());
        newAppointment.setAppointmentDate(appointment.getAppointmentDate());
        newAppointment.setDuration(currService.getDuration());
        newAppointment.setStatus(AppointmentStatus.PENDING);
        newAppointment.setCreatedDate(LocalDateTime.now());
        newAppointment.setSlot(currSlot);
        newAppointment.setClinicBranch(currClinicBranch);
        newAppointment.setDentist(currDentist);
        newAppointment.setService(currService);
        newAppointment.setCustomer(customer);

        appointmentRepository.save(newAppointment);

        mailService.sendCustomerAppointmentRequestConfirmationMail(customer, newAppointment);

        return new AppointmentViewDetailsResponse(newAppointment.getId(), newAppointment.getStatus().name(), customer.getId(),newAppointment.getCustomerName(), newAppointment.getCustomerAddress(),
                newAppointment.getCustomerPhone(), newAppointment.getCustomerDob(), newAppointment.getCustomerAge(),
                newAppointment.getCustomerEmail(), newAppointment.getAppointmentDate(),
                newAppointment.getDuration(), modelMapper.map(newAppointment.getSlot(), SlotDetailsResponse.class),
                modelMapper.map(newAppointment.getClinicBranch(), BranchSummaryResponse.class),
                modelMapper.map(newAppointment.getDentist(), DentistViewListResponse.class),
                modelMapper.map(newAppointment.getService(), ServiceViewListResponse.class), newAppointment.getCreatedDate());
//        return null;
    }

    @Override
    public AppointmentViewDetailsResponse viewDetailsAppointment(Long appointmentId) {

        Customer customer = customerRepository.findByUsername(authenticateService.getUserInfo().getUsername())
                .orElse(null);

        Appointment currAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Customer currCustomer = customerRepository.findById(currAppointment.getCustomer().getId())
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
        
        return new AppointmentViewDetailsResponse(currAppointment.getId(), currAppointment.getStatus().name(), currCustomer.getId() ,currAppointment.getCustomerName(), currAppointment.getCustomerAddress(), currAppointment.getCustomerPhone(),
                currAppointment.getCustomerDob(),currAppointment.getCustomerAge(), currAppointment.getCustomerEmail(), currAppointment.getAppointmentDate()
                , currService.getDuration(), modelMapper.map(currSlot, SlotDetailsResponse.class), modelMapper.map(currBranch, BranchSummaryResponse.class ), modelMapper.map(currDentist, DentistViewListResponse.class)
                , modelMapper.map(currService, ServiceViewListResponse.class), currAppointment.getCreatedDate());
    }

    @Override
    public AppointmentViewDetailsResponse cancelAppointment(Long appointmentId) {

        if (!authenticateService.getUserInfo().getRole().equals(UserType.CUSTOMER.toString()) &&
                !authenticateService.getUserInfo().getRole().equals(UserType.STAFF.toString())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

       Customer customer = customerRepository.findByUsername(authenticateService.getUserInfo().getUsername())
               .orElse(null);

        Appointment currAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

        Customer currCustomer = customerRepository.findById(currAppointment.getCustomer().getId())
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
                throw new ApiException(HttpStatus.BAD_REQUEST, "Can not cancel another customers' appointments");
            }
        }

        if (currAppointment.getStatus().equals(AppointmentStatus.CANCELED)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "The appointment is already canceled");
        }

        currAppointment.setStatus(AppointmentStatus.CANCELED);

        appointmentRepository.save(currAppointment);

        mailService.sendCustomerAppointmentCancelConfirmationMail(currCustomer, currAppointment);

        return new AppointmentViewDetailsResponse(currAppointment.getId(), currAppointment.getStatus().name(), currCustomer.getId() ,currAppointment.getCustomerName(), currAppointment.getCustomerAddress(), currAppointment.getCustomerPhone(),
                currAppointment.getCustomerDob(),currAppointment.getCustomerAge(), currAppointment.getCustomerEmail(), currAppointment.getAppointmentDate()
                , currService.getDuration(), modelMapper.map(currSlot, SlotDetailsResponse.class), modelMapper.map(currBranch, BranchSummaryResponse.class ), modelMapper.map(currDentist, DentistViewListResponse.class)
                , modelMapper.map(currService, ServiceViewListResponse.class), currAppointment.getCreatedDate());
    }
}
