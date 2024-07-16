package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.response.CustomerDetailRes;

public interface ICustomerService {
    CustomerDetailRes getCustomerDetail(Long id);
    CustomerDetailRes changeCustomerStatus(Long id);
}
