package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.*;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerRegisterResponse;
import com.example.dentalclinicschedulingplatform.payload.response.RefreshTokenResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.security.JwtService;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import com.example.dentalclinicschedulingplatform.service.IMailService;
import com.example.dentalclinicschedulingplatform.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticateService implements IAuthenticateService {
    private final JwtService jwtService;
    private final PasswordEncoder  passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final SystemAdminRepository systemAdminRepository;
    private final CustomerRepository customerRepository;
    private final DentistRepository dentistRepository;
    private final StaffRepository staffRepository;
    private final OwnerRepository ownerRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final IMailService mailService;

    public AuthenticationResponse authenticateAccount(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        var jwtToken = jwtService.generateToken(authentication);
        var refreshToken = generateRefreshToken(authentication);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        authenticationResponse.setRefreshToken(refreshToken.getRefreshToken());
        return authenticationResponse;
    }

    @Override
    @Transactional
    public CustomerRegisterResponse registerCustomerAccount(CustomerRegisterRequest request) {
        // add check if username already exists
        if(isUsernameExisted(request.getUsername()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username is already used");
        if(request.getDob().isAfter(LocalDate.now()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dob cannot be after present date!");
        Customer user = new Customer();
        modelMapper.map(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(true);
        user = customerRepository.save(user);
        mailService.sendCustomerRegistrationMail(user);
        return modelMapper.map(user, CustomerRegisterResponse.class);
    }

    @Override
    public UserInformationRes getUserInfo(){
        String role = SecurityUtils.getRoleName();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInformationRes res = new UserInformationRes();
        if(role.equals("ROLE_" + UserType.CUSTOMER)){
            Customer user = customerRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = mapUserRes(user);
        }else if(role.equals("ROLE_" + UserType.DENTIST)){
            Dentist user = dentistRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = mapUserRes(user);
        }else if(role.equals("ROLE_" + UserType.STAFF)){
            ClinicStaff user = staffRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = mapUserRes(user);
        }else if(role.equals("ROLE_" + UserType.OWNER)){
            ClinicOwner user = ownerRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = mapUserRes(user);
        } else if (role.equals("ROLE_" + UserType.ADMIN)) {
            SystemAdmin user = systemAdminRepository.findByUsername(name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = mapUserRes(user);
        }
        return res;
    }

    @Override
    public UserInformationRes updateUserInfo(UserInfoUpdateRequest request) {
        String role = SecurityUtils.getRoleName();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if(role.equals("ROLE_" + UserType.CUSTOMER)){
            var existingUser = customerRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            if(!existingUser.getUsername().equals(request.getUsername()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Username cannot be changed!");
            if(!existingUser.getEmail().equals(request.getEmail()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Email cannot be changed!");
            if(request.getDob().isAfter(LocalDate.now()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Dob cannot be after present date!");
            modelMapper.map(request, existingUser);
            var updated = customerRepository.save(existingUser);
            return mapUserRes(updated);
        }else if(role.equals("ROLE_" + UserType.DENTIST)){
            var existingUser = dentistRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            if(!existingUser.getUsername().equals(request.getUsername()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Username cannot be changed!");
            if(!existingUser.getEmail().equals(request.getEmail()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Email cannot be changed!");
            if(request.getDob().isAfter(LocalDate.now()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Dob cannot be after present date!");
            modelMapper.map(request, existingUser);
            var updated = dentistRepository.save(existingUser);
            return mapUserRes(updated);
        }else if(role.equals("ROLE_" + UserType.STAFF)){
            var existingUser = staffRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            if(!existingUser.getUsername().equals(request.getUsername()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Username cannot be changed!");
            if(!existingUser.getEmail().equals(request.getEmail()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Email cannot be changed!");
            if(request.getDob().isAfter(LocalDate.now()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Dob cannot be after present date!");
            modelMapper.map(request, existingUser);
            var updated = staffRepository.save(existingUser);
            return mapUserRes(updated);
        }else if(role.equals("ROLE_" + UserType.OWNER)){
            var existingUser = ownerRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            if(!existingUser.getUsername().equals(request.getUsername()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Username cannot be changed!");
            if(!existingUser.getEmail().equals(request.getEmail()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Email cannot be changed!");
            if(request.getDob().isAfter(LocalDate.now()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Dob cannot be after present date!");
            modelMapper.map(request, existingUser);
            var updated = ownerRepository.save(existingUser);
            return mapUserRes(updated);
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND, "Invalid role!");
        }

    }

    @Override
    public String changePassword(PasswordChangeRequest request) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityUtils.getRoleName();
        if(role.equals("ROLE_" + UserType.CUSTOMER)){
            var user = customerRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            if(checkPasswordChange(request.getOldPassword(), request.getNewPassword(), user.getPassword())){
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                customerRepository.save(user);
            }
        }else if(role.equals("ROLE_" + UserType.DENTIST)){
            var user = dentistRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            if(checkPasswordChange(request.getOldPassword(), request.getNewPassword(), user.getPassword())){
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                dentistRepository.save(user);
            }
        }else if(role.equals("ROLE_" + UserType.STAFF)){
            var user = staffRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            if(checkPasswordChange(request.getOldPassword(), request.getNewPassword(), user.getPassword())){
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                staffRepository.save(user);
            }
        }else if(role.equals("ROLE_" + UserType.OWNER)){
            var user = ownerRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            if(checkPasswordChange(request.getOldPassword(), request.getNewPassword(), user.getPassword())){
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                ownerRepository.save(user);
            }
        } else if (role.equals("ROLE_" + UserType.ADMIN)) {
            var user = systemAdminRepository.findByUsername(name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            if(checkPasswordChange(request.getOldPassword(), request.getNewPassword(), user.getPassword())){
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                systemAdminRepository.save(user);
            }
        }
        return "Password changed successfully";
    }

    @Override
    public boolean isUsernameOrEmailExisted(String username, String email) {
        return customerRepository.existsByUsernameOrEmail(username, email) ||
                dentistRepository.existsByEmailOrUsername(username, email) ||
                staffRepository.existsByEmailOrUsername(username, email) ||
                ownerRepository.existsByEmailOrUsername(username, email);
    }

    @Override
    public boolean isUsernameExisted(String username) {
        return customerRepository.existsByUsername(username) ||
                dentistRepository.existsByUsername(username) ||
                staffRepository.existsByUsername(username) ||
                ownerRepository.existsByUsername(username) ||
                systemAdminRepository.existsByUsername(username);
    }

    @Override
    public boolean checkPasswordChange(String requestOldPass, String requestNewPass, String userPass){
        if (!passwordEncoder.matches(requestOldPass, userPass)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Old password does not match!");
        }
        return true;
    }
    private RefreshToken generateRefreshToken(Authentication authentication) {
        String name = authentication.getName();
        Customer customer = customerRepository.findByUsername(name)
                .orElse(null);
        ClinicOwner owner = ownerRepository.findByUsername(name)
                .orElse(null);
        ClinicStaff staff = staffRepository.findByUsername(name)
                .orElse(null);
        Dentist dentist = dentistRepository.findByUsername(name)
                .orElse(null);
        SystemAdmin admin = systemAdminRepository.findByUsername(name)
                .orElse(null);

        RefreshToken refreshToken = new RefreshToken();

        if (customer != null) {
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(1));
            refreshToken.setCustomer(customer);
            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }else if (owner != null) {
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(1));
            refreshToken.setOwner(owner);
            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }else if (staff != null) {
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(1));
            refreshToken.setStaff(staff);
            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }else if (dentist != null) {
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(1));
            refreshToken.setDentist(dentist);
            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }else if (admin != null) {
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(1));
            refreshToken.setAdmin(admin);
            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        } else throw new ApiException(HttpStatus.NOT_FOUND, "User not found");
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Refresh token not found"));

        if (refreshToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token has expired");
        }

        Customer customer = refreshToken.getCustomer();
        ClinicOwner owner = refreshToken.getOwner();
        ClinicStaff staff = refreshToken.getStaff();
        Dentist dentist = refreshToken.getDentist();
        SystemAdmin admin = refreshToken.getAdmin();

        if (customer != null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + UserType.CUSTOMER));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    customer.getUsername(),
                    null,
                    authorities);
            String jwtToken = jwtService.generateToken(authentication);
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(1));
            refreshTokenRepository.save(refreshToken);
            RefreshTokenResponse response = new RefreshTokenResponse();
            response.setToken(jwtToken);
            response.setRefreshToken(refreshToken.getRefreshToken());
            return response;
        }else if (owner != null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" +UserType.ADMIN));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    owner.getUsername(),
                    null,
                    authorities);
            String jwtToken = jwtService.generateToken(authentication);
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(1));
            refreshTokenRepository.save(refreshToken);
            RefreshTokenResponse response = new RefreshTokenResponse();
            response.setToken(jwtToken);
            response.setRefreshToken(refreshToken.getRefreshToken());
            return response;
        }else if (staff != null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" +UserType.STAFF));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    staff.getUsername(),
                    null,
                    authorities);
            String jwtToken = jwtService.generateToken(authentication);
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(1));
            refreshTokenRepository.save(refreshToken);
            RefreshTokenResponse response = new RefreshTokenResponse();
            response.setToken(jwtToken);
            response.setRefreshToken(refreshToken.getRefreshToken());
            return response;
        }else if (dentist != null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" +UserType.DENTIST));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    dentist.getUsername(),
                    null,
                    authorities);
            String jwtToken = jwtService.generateToken(authentication);
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(1));
            refreshTokenRepository.save(refreshToken);
            RefreshTokenResponse response = new RefreshTokenResponse();
            response.setToken(jwtToken);
            response.setRefreshToken(refreshToken.getRefreshToken());
            return response;
        }else if (admin != null){
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" +UserType.ADMIN));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    admin.getUsername(),
                    null,
                    authorities);
            String jwtToken = jwtService.generateToken(authentication);
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(1));
            refreshTokenRepository.save(refreshToken);
            RefreshTokenResponse response = new RefreshTokenResponse();
            response.setToken(jwtToken);
            response.setRefreshToken(refreshToken.getRefreshToken());
            return response;
        }else throw new ApiException(HttpStatus.NOT_FOUND, "User not found");
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository.findByRefreshToken(request.getRefreshToken()).orElse (null);
        if (token != null) {
            refreshTokenRepository.delete(token);
        }
    }

    @Override
    @Transactional
    public AuthenticationResponse loginWithGoogle(LoginGoogleRequest request) {
        String token = request.getToken();
        try {
            URL url = new URL ("https://www.googleapis.com/oauth2/v3/userinfo");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection ();
            connection.setRequestMethod ("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            int responseCode = connection.getResponseCode ();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader (connection.getInputStream ()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close ();
                JsonParser jsonParser = JsonParserFactory.getJsonParser();
                Map<String, Object> jsonData = jsonParser.parseMap(response.toString());
                String email = (String) jsonData.get("email");
                String givenName = (String) jsonData.get("given_name");
                String picture = (String) jsonData.get("picture");
                Optional<Customer> customerOpt = customerRepository.findByUsernameOrEmail (email, email);
                Customer customer;
                if (customerOpt.isPresent()) {
                    customer = customerOpt.get();
                }
                else
                {
                    customer = new Customer();
                    customer.setEmail(email);
                    customer.setUsername(email);
                    customer.setFullName (givenName);
                    customer.setAvatar (picture);
                    customer.setGender ("Male");
                    customer.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    customer.setStatus(true);
                    customerRepository.save(customer);
                }
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        customer.getUsername(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + UserType.CUSTOMER)));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                String jwtToken = jwtService.generateToken(authentication);
                RefreshToken refreshToken = generateRefreshToken(authentication);

                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setToken(jwtToken);
                authenticationResponse.setRefreshToken(refreshToken.getRefreshToken());
                return authenticationResponse;
            }
            else
            {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Google token");
            }
        } catch (Exception e) {
            throw new ApiException (HttpStatus.INTERNAL_SERVER_ERROR, "Login google failed");
        }
    }

    private UserInformationRes mapUserRes(Object user){
        UserInformationRes res = new UserInformationRes();
        if(user instanceof Customer){
            res.setUsername(((Customer) user).getUsername());
            res.setFullName(((Customer) user).getFullName());
            res.setEmail(((Customer) user).getEmail());
            res.setAddress(((Customer) user).getAddress());
            res.setPhone(((Customer) user).getPhone());
            res.setDob(((Customer) user).getDob());
            res.setGender(((Customer) user).getGender());
            res.setAvatar(((Customer) user).getAvatar());
            res.setRole(UserType.CUSTOMER.toString());
        }else if(user instanceof Dentist){
            res.setUsername(((Dentist) user).getUsername());
            res.setFullName(((Dentist) user).getFullName());
            res.setEmail(((Dentist) user).getEmail());
            res.setAddress(((Dentist) user).getAddress());
            res.setPhone(((Dentist) user).getPhone());
            res.setDob(((Dentist) user).getDob());
            res.setGender(((Dentist) user).getGender());
            res.setAvatar(((Dentist) user).getAvatar());
            res.setClinicId(((Dentist) user).getClinicBranch().getClinic().getClinicId());
            res.setClinicId(((Dentist) user).getClinicBranch().getBranchId());
            res.setRole(UserType.DENTIST.toString());
        }else if(user instanceof ClinicStaff){
            res.setUsername(((ClinicStaff) user).getUsername());
            res.setFullName(((ClinicStaff) user).getFullName());
            res.setEmail(((ClinicStaff) user).getEmail());
            res.setAddress(((ClinicStaff) user).getAddress());
            res.setPhone(((ClinicStaff) user).getPhone());
            res.setDob(((ClinicStaff) user).getDob());
            res.setGender(((ClinicStaff) user).getGender());
            res.setAvatar(((ClinicStaff) user).getAvatar());
            res.setClinicId(((ClinicStaff) user).getClinicBranch().getClinic().getClinicId());
            res.setClinicId(((ClinicStaff) user).getClinicBranch().getBranchId());
            res.setRole(UserType.STAFF.toString());
        }else if(user instanceof ClinicOwner) {
            res.setUsername(((ClinicOwner) user).getUsername());
            res.setFullName(((ClinicOwner) user).getFullName());
            res.setEmail(((ClinicOwner) user).getEmail());
            res.setAddress(((ClinicOwner) user).getAddress());
            res.setPhone(((ClinicOwner) user).getPhone());
            res.setDob(((ClinicOwner) user).getDob());
            res.setGender(((ClinicOwner) user).getGender());
            res.setAvatar(((ClinicOwner) user).getAvatar());
            res.setClinicId(((ClinicOwner) user).getClinic().getClinicId());
            res.setRole(UserType.OWNER.toString());
        }else if(user instanceof SystemAdmin){
            res.setUsername(((SystemAdmin) user).getUsername());
            res.setRole(UserType.ADMIN.toString());
        }else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid user type!");
        }
        return res;
    }
}
