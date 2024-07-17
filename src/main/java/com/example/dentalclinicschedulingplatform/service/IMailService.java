package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.entity.*;

public interface IMailService {
    void sendCustomerRegistrationMail(Customer customer);
    void sendClinicRequestConfirmationMail(String fullName, String email);
    void sendClinicRequestApprovalMail(ClinicOwner owner, String password);
    void sendClinicRequestRejectionMail(String fullName, String email);
    void sendStaffRequestApprovalMail(ClinicStaff staff, String branchName, String password);
    void sendStaffRequestRejectionMail(ClinicStaff staff, String branchName);
    void senDentistRequestConfirmationMail(Dentist dentist, ClinicOwner owner);
    void sendDentistRequestApprovalMail(Dentist dentist, String password, ClinicOwner owner);
    void sendCustomerAppointmentRequestConfirmationMail(Customer customer, Appointment appointment);
    void sendCustomerAppointmentCancelConfirmationMail(Customer customer, Appointment appointment, String reason);
    void sendPasswordRecoveryMail(String email, String verificationCode);
    void sendRemindAppointmentMail(Appointment appointment);

//    void sendBranchRequestApprovalMail(ClinicOwner owner);
//    void sendBranchRequestRejectionMail(ClinicOwner owner);
}
