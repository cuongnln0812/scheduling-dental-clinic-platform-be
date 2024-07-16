package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.response.CustomerViewResponse;

public interface ICustomerService {
    CustomerViewResponse getCustomerDetail(Long id);

    CustomerViewResponse activateDeactivateCustomer(Long customerId);
}
