package com.example.dentalclinicschedulingplatform.security;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SystemAdminRepository systemAdminRepository;
    private final CustomerRepository customerRepository;
    private final DentistRepository dentistRepository;
    private final StaffRepository staffRepository;
    private final OwnerRepository ownerRepository;

    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
        Optional<SystemAdmin> optionalSystemAdmin = systemAdminRepository.findByUsername(user);
        if (optionalSystemAdmin.isPresent()) {
            SystemAdmin systemAdmin = optionalSystemAdmin.get();
            if(!systemAdmin.isStatus()) throw new ApiException(HttpStatus.FORBIDDEN, "Account is not available!");
            return new User(systemAdmin.getUsername(), systemAdmin.getPassword(), getAuthorities(UserType.ADMIN));
        }

        Optional<Customer> optionalCustomer = customerRepository.findByUsernameOrEmail(user, user);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            if(!customer.isStatus()) throw new ApiException(HttpStatus.FORBIDDEN, "Account is not available!");
            return new User(customer.getUsername(), customer.getPassword(), getAuthorities(UserType.CUSTOMER));
        }

        Optional<Dentist> optionalDentist = dentistRepository.findByUsernameOrEmail(user, user);
        if (optionalDentist.isPresent()) {
            Dentist dentist = optionalDentist.get();
            if(!dentist.getStatus().equals(Status.ACTIVE)) throw new ApiException(HttpStatus.FORBIDDEN, "Account is not available!");
            return new User(dentist.getUsername(), dentist.getPassword(), getAuthorities(UserType.DENTIST));
        }

        Optional<ClinicStaff> optionalStaff = staffRepository.findByUsernameOrEmail(user, user);
        if (optionalStaff.isPresent()) {
            ClinicStaff staff = optionalStaff.get();
            if(!staff.getStatus().equals(Status.ACTIVE)) throw new ApiException(HttpStatus.FORBIDDEN, "Account is not available!");
            return new User(staff.getUsername(), staff.getPassword(), getAuthorities(UserType.STAFF));
        }

        Optional<ClinicOwner> optionalOwner = ownerRepository.findByUsernameOrEmail(user, user);
        if (optionalOwner.isPresent()) {
            ClinicOwner owner = optionalOwner.get();
            if(!owner.getStatus().equals(Status.ACTIVE)) throw new ApiException(HttpStatus.FORBIDDEN, "Account is not available!");
            return new User(owner.getUsername(), owner.getPassword(), getAuthorities(UserType.OWNER));
        }
        throw new UsernameNotFoundException("Account is not found!");
    }

    private List<GrantedAuthority> getAuthorities(UserType userType) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + userType.toString()));
        return authorities;
    }
}
