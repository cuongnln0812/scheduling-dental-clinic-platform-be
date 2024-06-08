package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Dentist;
import com.example.dentalclinicschedulingplatform.entity.Status;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.DentistCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.DentistUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.DentistDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistListResponse;
import com.example.dentalclinicschedulingplatform.repository.DentistRepository;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import com.example.dentalclinicschedulingplatform.service.IDentistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DentistService implements IDentistService {

    private final ModelMapper modelMapper;
    private final DentistRepository dentistRepository;
    private final IAuthenticateService authenticateService;

    @Override
    public Page<DentistListResponse> getDentistListByBranch(Long branchId, int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Page<Dentist> dentistList;
        if(branchId != null){
            dentistList = dentistRepository.findAllByClinicBranch_Id(branchId, pageRequest);
        }else dentistList = dentistRepository.findAll(pageRequest);

        return dentistList.map(dentist -> modelMapper.map(dentist,DentistListResponse.class));
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
        Dentist dentist = new Dentist();
        modelMapper.map(request, dentist);
        dentist.setStatus(Status.PENDING);
        dentist = dentistRepository.save(dentist);
        return modelMapper.map(dentist, DentistDetailResponse.class);
    }

    @Override
    @Transactional
    public DentistDetailResponse updateDentistDetails(DentistUpdateRequest request) {
        Dentist existingDentist = dentistRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", request.getId()));
        Status.isValid(request.getStatus());
        modelMapper.map(request, existingDentist);
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
