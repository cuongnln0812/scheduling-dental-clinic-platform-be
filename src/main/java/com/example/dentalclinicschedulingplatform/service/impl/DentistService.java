package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Dentist;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.DentistDetailRequest;
import com.example.dentalclinicschedulingplatform.payload.response.DentistDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistListResponse;
import com.example.dentalclinicschedulingplatform.repository.DentistRepository;
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
@Transactional
@RequiredArgsConstructor
public class DentistService implements IDentistService {

    private final ModelMapper modelMapper;
    private final DentistRepository dentistRepository;

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
    public DentistDetailResponse updateDentistDetails(DentistDetailRequest request) {
        Dentist dentist = dentistRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist", "id", request.getId()));
        dentist = modelMapper.map(request, Dentist.class);
        dentist = dentistRepository.save(dentist);
        return modelMapper.map(dentist, DentistDetailResponse.class);
    }


}
