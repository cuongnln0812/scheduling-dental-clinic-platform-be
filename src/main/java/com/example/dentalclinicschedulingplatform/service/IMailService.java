package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.ClinicOwner;
import com.example.dentalclinicschedulingplatform.entity.ClinicStaff;
import com.example.dentalclinicschedulingplatform.entity.Customer;
import com.example.dentalclinicschedulingplatform.entity.Dentist;

public interface IMailService {
    void sendCustomerRegistrationMail(Customer customer);
    void sendClinicRequestConfirmationMail(String fullName, String email);
    void sendClinicRequestApprovalMail(ClinicOwner owner, String password);
    void sendClinicRequestRejectionMail(String fullName, String email);
    void sendStaffRequestApprovalMail(ClinicStaff staff, String branchName, String password);
    void sendStaffRequestRejectionMail(ClinicStaff staff, String branchName);
    void sendDentistRequestApprovalMail(Dentist dentist, String password);
}
