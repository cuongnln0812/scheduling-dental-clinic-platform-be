package com.example.dentalclinicschedulingplatform.security;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.repository.DentistRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.repository.StaffRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final DentistRepository dentistRepository;
    private final StaffRepository staffRepository;
    private final OwnerRepository ownerRepository;
    private UserType userType;

    public CustomUserDetailsService(CustomerRepository customerRepository,
                                    DentistRepository dentistRepository,
                                    StaffRepository staffRepository,
                                    OwnerRepository ownerRepository) {
        this.customerRepository = customerRepository;
        this.dentistRepository = dentistRepository;
        this.staffRepository = staffRepository;
        this.ownerRepository = ownerRepository;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if(userType==UserType.CUSTOMER) {
            Customer customer = customerRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Customer Email "+ email+ "not found"));

            SimpleGrantedAuthority customerAuth = new SimpleGrantedAuthority(UserType.CUSTOMER.toString());
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(customerAuth);
            return new User(customer.getEmail(), customer.getPassword(), authorities);
        } else if(userType == UserType.DENTIST) {
            Dentist dentist = dentistRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Dentist Email "+ email+ "not found"));

            SimpleGrantedAuthority dentistAuth = new SimpleGrantedAuthority(UserType.DENTIST.toString());
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(dentistAuth);
            return new User(dentist.getEmail(), dentist.getPassword(), authorities);
        } else if(userType == UserType.STAFF) {
            ClinicStaff staff = staffRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Staff Email "+ email+ "not found"));

            SimpleGrantedAuthority staffAuth = new SimpleGrantedAuthority(UserType.STAFF.toString());
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(staffAuth);
            return new User(staff.getEmail(), staff.getPassword(), authorities);
        } else if(userType == UserType.OWNER) {
            ClinicOwner owner = ownerRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Owner Email "+ email+ "not found"));

            SimpleGrantedAuthority ownerAuth = new SimpleGrantedAuthority(UserType.OWNER.toString());
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(ownerAuth);
            return new User(owner.getEmail(), owner.getPassword(), authorities);
        }
        return null;
    }
}
