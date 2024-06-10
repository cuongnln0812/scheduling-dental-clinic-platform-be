package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.Status;
import com.example.dentalclinicschedulingplatform.payload.request.DentistCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.DentistUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.DentistDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

public interface IDentistService{
    Page<DentistListResponse> getDentistListByBranch(Long id, int page, int size, String dir, String by);
    Page<DentistListResponse> getPendingDentistList(int page, int size);
    DentistDetailResponse approveDentistAccount(Long id, boolean isApproved);
    DentistDetailResponse getDentistDetail(Long id);
    DentistDetailResponse createDentist(DentistCreateRequest request);
    DentistDetailResponse updateDentistDetails(DentistUpdateRequest request);
    DentistDetailResponse removeDentist(Long id);
}
