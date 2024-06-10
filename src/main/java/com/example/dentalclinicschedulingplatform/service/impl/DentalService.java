package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.ServiceCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.ServiceUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewDetailsResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewListResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.IDentalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@org.springframework.stereotype.Service
@Transactional
@RequiredArgsConstructor
public class DentalService implements IDentalService {

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ClinicRepository clinicRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ClinicBranchRepository clinicBranchRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public List<ServiceViewDetailsResponse> viewServicesByCategoryId(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category does not exist"));

        List<Service> serviceList = serviceRepository.findServicesByCategoryId(categoryId);
        List<ServiceViewDetailsResponse> serviceViewDetailsResponseList = new ArrayList<>();
        for (Service serviceItem : serviceList) {
            if (serviceItem.getStatus().equals(Status.ACTIVE)){
                serviceViewDetailsResponseList.add(new ServiceViewDetailsResponse(serviceItem.getId(), serviceItem.getServiceName(), serviceItem.getDescription(),
                        serviceItem.getUnitOfPrice(), serviceItem.getMinimumPrice(), serviceItem.getMaximumPrice(), serviceItem.getDuration(),serviceItem.getServiceType(), serviceItem.getStatus()));
            }
        }
        return serviceViewDetailsResponseList;
    }

    @Override
    public List<ServiceViewListResponse> viewServicesByClinic(Long clinicId, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);
        List<ServiceViewListResponse> serviceViewListResponses = new ArrayList<>();

        if (clinicId == null) {
            Page<Service> servicePage = serviceRepository.findAll(pageRequest);
            for (Service serviceItem: servicePage) {
                serviceViewListResponses.add(modelMapper.map(serviceItem, ServiceViewListResponse.class));
            }
            serviceViewListResponses.sort(Comparator.comparing(ServiceViewListResponse::getId));
            return serviceViewListResponses;
        }else {
            Clinic clinic = clinicRepository.findById(clinicId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

            Page<Service> serviceList = serviceRepository.findServicesByClinicId(clinicId, pageRequest);

            for (Service serviceItem :serviceList) {
                serviceViewListResponses.add(modelMapper.map(serviceItem, ServiceViewListResponse.class));
            }
            serviceViewListResponses.sort(Comparator.comparing(ServiceViewListResponse::getId));
            return serviceViewListResponses;
        }
    }

    @Override
    public ServiceViewDetailsResponse createNewService(UserInformationRes userInformationRes, ServiceCreateRequest request) {
        if (!userInformationRes.getRole().equals(UserType.OWNER.toString())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

        ClinicOwner owner = ownerRepository.findByUsernameOrEmail(userInformationRes.getUsername(), userInformationRes.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner does not exist"));

        Clinic clinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

        Category currCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category does not exist"));

        List<Category> categories = categoryRepository.findCategoriesByClinicId(clinic.getId());

        if (!categories.contains(currCategory)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Category does not belong to current clinic");
        }

        Service existingService = serviceRepository.findByServiceNameAndClinicId(request.getServiceName(), clinic.getId());
        if (existingService != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Service name is already existed");
        }

        Service newService = new Service();
        newService.setServiceName(request.getServiceName());
        newService.setDescription(request.getDescription());
        newService.setUnitOfPrice(request.getUnitOfPrice());
        newService.setMinimumPrice(request.getMinimumPrice());
        newService.setMaximumPrice(request.getMaximumPrice());
        newService.setDuration(request.getDuration());
        newService.setServiceType(request.getServiceType());
        newService.setStatus(Status.PENDING);
        newService.setCreatedBy(owner.getEmail());
        newService.setCreatedDate(LocalDateTime.now());
        newService.setCategory(currCategory);
        newService.setClinic(clinic);

        serviceRepository.save(newService);

        return modelMapper.map(newService, ServiceViewDetailsResponse.class);
    }

    @Override
    public ServiceViewDetailsResponse updateService(UserInformationRes userInformationRes, ServiceUpdateRequest request) {
        if (!userInformationRes.getRole().equals(UserType.OWNER.toString()) && !userInformationRes.getRole().equals(UserType.STAFF.toString())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

        if (Arrays.stream(Status.values())
                .noneMatch(e -> e.equals(request.getStatus()))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only Status (ACTIVE, INACTIVE, PENDING)");
        }

        ClinicOwner owner = ownerRepository.findByUsernameOrEmail(userInformationRes.getUsername(), userInformationRes.getEmail())
                .orElse(null);

        Service updatedService = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service does not exist"));

        if (owner == null) {
            ClinicStaff staff = staffRepository.findByUsernameOrEmail(userInformationRes.getUsername(), userInformationRes.getEmail())
                    .orElse(null);
            if (staff == null) throw new ApiException(HttpStatus.NOT_FOUND, "User does not exist");

            Long clinicId = staffRepository.getClinicIdOfStaff(staff.getId());

            Category currCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category does not exist"));

            List<Category> categories = categoryRepository.findCategoriesByClinicId(clinicId);

            List<Service> services = serviceRepository.findServicesByClinicId(clinicId);

            if (!services.contains(updatedService)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Services does not belong to current clinic");
            }

            if (!categories.contains(currCategory)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Category does not belong to current clinic");
            }

            if (!updatedService.getServiceName().equals(request.getServiceName())){
                Service existingService = serviceRepository.findByServiceNameAndClinicId(request.getServiceName(), clinicId);
                if (existingService != null) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Service name is already existed");
                }
            }

            updatedService.setServiceName(request.getServiceName());
            updatedService.setDescription(request.getDescription());
            updatedService.setUnitOfPrice(request.getUnitOfPrice());
            updatedService.setMinimumPrice(request.getMinimumPrice());
            updatedService.setMaximumPrice(request.getMaximumPrice());
            updatedService.setDuration(request.getDuration());
            updatedService.setServiceType(request.getServiceType());
            updatedService.setStatus(Status.PENDING);
            updatedService.setModifiedBy(staff.getEmail());
            updatedService.setModifiedDate(LocalDateTime.now());
            updatedService.setCategory(currCategory);
            updatedService.setStatus(request.getStatus());

            serviceRepository.save(updatedService);
            return new ServiceViewDetailsResponse(updatedService.getId(), updatedService.getServiceName(), updatedService.getDescription(),
                    updatedService.getUnitOfPrice(), updatedService.getMinimumPrice(), updatedService.getMaximumPrice(), updatedService.getDuration(), updatedService.getServiceType(), updatedService.getStatus());
        }

        Clinic clinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

        Category currCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category does not exist"));

        List<Category> categories = categoryRepository.findCategoriesByClinicId(clinic.getId());

        List<Service> services = serviceRepository.findServicesByClinicId(clinic.getId());

        if (!services.contains(updatedService)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Services does not belong to current clinic");
        }

        if (!categories.contains(currCategory)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Category does not belong to current clinic");
        }

        if (!updatedService.getServiceName().equals(request.getServiceName())){
            Service existingService = serviceRepository.findByServiceNameAndClinicId(request.getServiceName(), clinic.getId());
            if (existingService != null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Service name is already existed");
            }
        }

        updatedService.setServiceName(request.getServiceName());
        updatedService.setDescription(request.getDescription());
        updatedService.setUnitOfPrice(request.getUnitOfPrice());
        updatedService.setMinimumPrice(request.getMinimumPrice());
        updatedService.setMaximumPrice(request.getMaximumPrice());
        updatedService.setDuration(request.getDuration());
        updatedService.setServiceType(request.getServiceType());
        updatedService.setStatus(request.getStatus());
        updatedService.setModifiedBy(owner.getEmail());
        updatedService.setModifiedDate(LocalDateTime.now());
        updatedService.setCategory(currCategory);

        serviceRepository.save(updatedService);
        return new ServiceViewDetailsResponse(updatedService.getId(), updatedService.getServiceName(), updatedService.getDescription(),
                updatedService.getUnitOfPrice(), updatedService.getMinimumPrice(), updatedService.getMaximumPrice(), updatedService.getDuration(),updatedService.getServiceType(), updatedService.getStatus());
    }

    @Override
    public ServiceViewDetailsResponse deleteService(UserInformationRes userInformation, Long serviceId) {
        if (!userInformation.getRole().equals(UserType.OWNER.toString())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

        Service deletedService = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Service does not exist"));

        ClinicOwner owner = ownerRepository.findByUsernameOrEmail(userInformation.getUsername(), userInformation.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner does not exist"));

        Clinic currClinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

        List<Service> services = serviceRepository.findServicesByClinicId(currClinic.getId());

        if (!services.contains(deletedService)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Service does not belong to current clinic");
        }

        if (deletedService.getStatus().equals(Status.ACTIVE)) {
            deletedService.setStatus(Status.INACTIVE);
        }else {
            throw new ApiException(HttpStatus.CONFLICT, "The service is already been deactivated or is pending");
        }

        deletedService.setModifiedDate(LocalDateTime.now());
        deletedService.setModifiedBy(owner.getEmail());

        serviceRepository.save(deletedService);

        return new ServiceViewDetailsResponse(deletedService.getId(), deletedService.getServiceName(), deletedService.getDescription(),
                deletedService.getUnitOfPrice(), deletedService.getMinimumPrice(), deletedService.getMaximumPrice(), deletedService.getDuration(), deletedService.getServiceType(), deletedService.getStatus());
    }
}
