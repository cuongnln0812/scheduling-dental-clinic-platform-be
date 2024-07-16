package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.response.AccountListResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerViewResponse;
import org.springframework.data.domain.Page;

public interface IAccountService {
    Page<AccountListResponse> getAllAccount(int page, int size);
}
