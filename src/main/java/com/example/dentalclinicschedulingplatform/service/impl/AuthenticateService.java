package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.exception.ResourceNotFoundException;
import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CustomerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.request.PasswordChangeRequest;
import com.example.dentalclinicschedulingplatform.payload.request.UserInfoUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.RefreshTokenRequest;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
        if(isUsernameOrEmailExisted(request.getUsername(), request.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username/Email is already used");
        if(request.getDob().isAfter(LocalDate.now()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dob cannot be after present date!");
        Customer user = new Customer();
        modelMapper.map(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(true);
        user = customerRepository.save(user);
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
//        message.setTo(user.getEmail());
//        // Set a meaningful message
//        message.setSubject("[F-Dental] - Tài khoản được tạo thành công");
//        String body = "Kính gửi " + user.getUsername() + ",\n\n" +
//                "Cảm ơn bạn đã tạo tài khoản trên " + "F-Dental" + "! Chúng tôi rất vui mừng được chào đón bạn đến với cộng đồng của mình và hỗ trợ bạn đạt được mục tiêu sức khỏe răng miệng.\n\n" +
//                "Để bắt đầu:\n\n" +
//                "1. Hoàn thành hồ sơ của bạn: Truy cập cài đặt tài khoản để thêm thông tin cá nhân, bao gồm chi tiết bảo hiểm nha khoa của bạn. Điều này sẽ giúp chúng tôi đơn giản hóa quy trình đặt lịch hẹn của bạn.\n\n" +
//                "2. Đặt lịch hẹn đầu tiên: Duyệt qua danh sách nha sĩ có sẵn và tìm thời gian phù hợp với lịch trình của bạn. Bạn có thể dễ dàng đặt lịch hẹn trực tuyến hoặc bằng cách gọi điện đến trung tâm chăm sóc khách hàng của chúng tôi.\n\n" +
//                "3. Khám phá các nguồn tài nguyên của chúng tôi: Chúng tôi cung cấp nhiều nguồn tài nguyên hữu ích để hỗ trợ hành trình sức khỏe răng miệng của bạn, bao gồm các bài viết, video và mẹo về cách duy trì vệ sinh răng miệng tốt.\n\n" +
//                "Chúng tôi luôn sẵn sàng hỗ trợ:\n\n" +
//                "Nếu bạn có bất kỳ câu hỏi nào hoặc cần trợ giúp về tài khoản của mình, vui lòng liên hệ với đội ngũ dịch vụ khách hàng thân thiện của chúng tôi. Chúng tôi luôn sẵn lòng giúp đỡ!\n\n" +
//                "Cảm ơn bạn đã chọn " + "F-Dental" + "!\n\n" +
//                "Trân trọng,\n\n" +
//                "Đội ngũ F-Dental";
//        message.setText(body);
//
//        // Send the email (assuming you have a mailSender bean configured)
//        mailSender.send(message);
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
            res = modelMapper.map(user, UserInformationRes.class);
            res.setRole(UserType.CUSTOMER.toString());
        }else if(role.equals("ROLE_" + UserType.DENTIST)){
            Dentist user = dentistRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = modelMapper.map(user, UserInformationRes.class);
            res.setRole(UserType.DENTIST.toString());
        }else if(role.equals("ROLE_" + UserType.STAFF)){
            ClinicStaff user = staffRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = modelMapper.map(user, UserInformationRes.class);
            res.setRole(UserType.STAFF.toString());
        }else if(role.equals("ROLE_" + UserType.OWNER)){
            ClinicOwner user = ownerRepository.findByUsernameOrEmail(name, name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = modelMapper.map(user, UserInformationRes.class);
            res.setRole(UserType.OWNER.toString());
        } else if (role.equals("ROLE_" + UserType.ADMIN)) {
            SystemAdmin user = systemAdminRepository.findByUsername(name)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
            res = modelMapper.map(user, UserInformationRes.class);
            res.setRole(UserType.ADMIN.toString());
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
            return modelMapper.map(updated, UserInformationRes.class);
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
            return modelMapper.map(updated, UserInformationRes.class);
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
            return modelMapper.map(updated, UserInformationRes.class);
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
            return modelMapper.map(updated, UserInformationRes.class);
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


}
