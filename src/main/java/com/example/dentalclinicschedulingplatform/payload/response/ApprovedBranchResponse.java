package com.example.dentalclinicschedulingplatform.payload.response;

import com.example.dentalclinicschedulingplatform.entity.Status;
import jakarta.persistence.Column;

public class ApprovedBranchResponse {
    private String branchName;
    private String address;
    private String city;
    private String phone;
    private boolean isMain;
    private Status status;
}
