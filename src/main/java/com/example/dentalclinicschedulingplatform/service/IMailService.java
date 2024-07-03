package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.*;

public interface IMailService {
    void sendCustomerRegistrationMail(Customer customer);
    void sendClinicRequestConfirmationMail(String fullName, String email);
    void sendClinicRequestApprovalMail(ClinicOwner owner, String password);
    void sendClinicRequestRejectionMail(String fullName, String email);
    void sendStaffRequestApprovalMail(ClinicStaff staff, String branchName, String password);
    void sendStaffRequestRejectionMail(ClinicStaff staff, String branchName);
    void sendDentistRequestApprovalMail(Dentist dentist, String password);
    void sendCustomerAppointmentRequestConfirmationMail(Customer customer, Appointment appointment);

//    void sendBranchRequestApprovalMail(ClinicOwner owner);
//    void sendBranchRequestRejectionMail(ClinicOwner owner);
}
