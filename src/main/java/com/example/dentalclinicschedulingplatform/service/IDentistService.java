package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.DentistDetailRequest;
import com.example.dentalclinicschedulingplatform.payload.response.DentistDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistListResponse;
import org.springframework.data.domain.Page;

public interface IDentistService{
    Page<DentistListResponse> getDentistListByBranch(Long id, int page, int size);
    DentistDetailResponse getDentistDetail(Long id);
    DentistDetailResponse updateDentistDetails(DentistDetailRequest request);
}
