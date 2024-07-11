package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.DentistCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.DentistUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.DentistDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistListResponse;
import com.example.dentalclinicschedulingplatform.payload.response.DentistViewListResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface IDentistService{
    Page<DentistListResponse> getDentistListByBranch(Long id, int page, int size, String dir, String by);
    Page<DentistListResponse> getDentistListByClinic(Long id, int page, int size);
    Page<DentistListResponse> getPendingDentistList(int page, int size);
    DentistDetailResponse approveDentistAccount(Long id, boolean isApproved);
    DentistDetailResponse getDentistDetail(Long id);
    DentistDetailResponse createDentist(DentistCreateRequest request);
    DentistDetailResponse updateDentistDetails(DentistUpdateRequest request);
    DentistDetailResponse removeDentist(Long id);
    DentistDetailResponse reactivateDentist(Long id);
    List<DentistViewListResponse> getAvailableDentistOfDateByBranch(Long branchId, LocalDate date, Long slotId);
    List<DentistViewListResponse> getAvailableDentistOfDateByBranchForUpdatingAppointment(Long branchId, LocalDate date, Long slotId, Long appointmentId);
}
