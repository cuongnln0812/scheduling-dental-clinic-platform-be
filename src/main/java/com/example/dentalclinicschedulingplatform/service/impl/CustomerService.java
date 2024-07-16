package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Customer;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerDetailRes;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;

    @Override
    public CustomerDetailRes getCustomerDetail(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return modelMapper.map(customer, CustomerDetailRes.class);
    }

    @Override
    public CustomerDetailRes changeCustomerStatus(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        customer.setStatus(!customer.isStatus());
        customer = customerRepository.save(customer);
        CustomerDetailRes res = modelMapper.map(customer, CustomerDetailRes.class);
        res.setStatus(customer.isStatus() ? "ACTIVE" : "INACTIVE");
        return res;
    }
}
