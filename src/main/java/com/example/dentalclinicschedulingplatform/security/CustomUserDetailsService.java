package com.example.dentalclinicschedulingplatform.security;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.repository.DentistRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.repository.StaffRepository;
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
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final DentistRepository dentistRepository;
    private final StaffRepository staffRepository;
    private final OwnerRepository ownerRepository;

    public CustomUserDetailsService(CustomerRepository customerRepository,
                                    DentistRepository dentistRepository,
                                    StaffRepository staffRepository,
                                    OwnerRepository ownerRepository) {
        this.customerRepository = customerRepository;
        this.dentistRepository = dentistRepository;
        this.staffRepository = staffRepository;
        this.ownerRepository = ownerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findByUsernameOrEmail(user, user);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            if(!customer.isStatus()) throw new ApiException(HttpStatus.FORBIDDEN, "Customer is forbidden!");
            return new User(customer.getEmail(), customer.getPassword(), getAuthorities(UserType.CUSTOMER));
        }

        Optional<Dentist> optionalDentist = dentistRepository.findByUsernameOrEmail(user, user);
        if (optionalDentist.isPresent()) {
            Dentist dentist = optionalDentist.get();
            if(dentist.getStatus().equals(Status.INACTIVE)) throw new ApiException(HttpStatus.FORBIDDEN, "Dentist is forbidden!");
            return new User(dentist.getEmail(), dentist.getPassword(), getAuthorities(UserType.DENTIST));
        }

        Optional<ClinicStaff> optionalStaff = staffRepository.findByUsernameOrEmail(user, user);
        if (optionalStaff.isPresent()) {
            ClinicStaff staff = optionalStaff.get();
            if(staff.getStatus().equals(Status.INACTIVE)) throw new ApiException(HttpStatus.FORBIDDEN, "Staff is forbidden!");
            return new User(staff.getEmail(), staff.getPassword(), getAuthorities(UserType.STAFF));
        }

        Optional<ClinicOwner> optionalOwner = ownerRepository.findByUsernameOrEmail(user, user);
        if (optionalOwner.isPresent()) {
            ClinicOwner owner = optionalOwner.get();
            if(owner.getStatus().equals(Status.INACTIVE)) throw new ApiException(HttpStatus.FORBIDDEN, "Owner is forbidden!");
            return new User(owner.getEmail(), owner.getPassword(), getAuthorities(UserType.OWNER));
        }
        throw new UsernameNotFoundException("User account not found");
    }

    private List<GrantedAuthority> getAuthorities(UserType userType) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + userType.toString()));
        return authorities;
    }
}
