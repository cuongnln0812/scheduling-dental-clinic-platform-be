package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.payload.request.BranchCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.BranchUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.BranchDetailResponse;
import com.example.dentalclinicschedulingplatform.payload.response.BranchSummaryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBranchService {
    ClinicBranch createMainBranch(Clinic clinic);
    BranchDetailResponse createBranch(BranchCreateRequest request);

//    BranchDetailResponse approveNewBranch(Long id, boolean isApproved);
    Page<BranchSummaryResponse> getBranchPendingList(int page, int size);
    List<BranchSummaryResponse> getBranchByClinic(Long clinicId);
    BranchSummaryResponse getBranchPendingDetail(Long branchId);
    BranchSummaryResponse updateBranch(BranchUpdateRequest request);
    BranchSummaryResponse deleteBranch(Long branchId);
    BranchDetailResponse viewBranch(Long id);
}
