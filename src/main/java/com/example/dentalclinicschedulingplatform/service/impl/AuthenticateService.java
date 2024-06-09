package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.AuthenticationRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CustomerRegisterRequest;
import com.example.dentalclinicschedulingplatform.payload.response.AuthenticationResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CustomerRegisterResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.repository.DentistRepository;
import com.example.dentalclinicschedulingplatform.repository.OwnerRepository;
import com.example.dentalclinicschedulingplatform.repository.StaffRepository;
import com.example.dentalclinicschedulingplatform.security.JwtService;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import com.example.dentalclinicschedulingplatform.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticateService implements IAuthenticateService {
    private final JwtService jwtService;
    private final PasswordEncoder  passwordEncoder;
    private final ModelMapper modelMapper;
    private final JavaMailSender mailSender;
    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final DentistRepository dentistRepository;
    private final StaffRepository staffRepository;
    private final OwnerRepository ownerRepository;

    public AuthenticationResponse authenticateAccount(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()));

        var jwtToken = jwtService.generateToken(authentication);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        message.setTo(user.getEmail());
        // Set a meaningful message
        message.setSubject("[FAMS] - Tài khoản được tạo thành công");
        String body = "Kính gửi " + user.getUsername() + ",\n\n" +
                "Cảm ơn bạn đã tạo tài khoản trên " + "F-Dental" + "! Chúng tôi rất vui mừng được chào đón bạn đến với cộng đồng của mình và hỗ trợ bạn đạt được mục tiêu sức khỏe răng miệng.\n\n" +
                "Để bắt đầu:\n\n" +
                "1. Hoàn thành hồ sơ của bạn: Truy cập cài đặt tài khoản để thêm thông tin cá nhân, bao gồm chi tiết bảo hiểm nha khoa của bạn. Điều này sẽ giúp chúng tôi đơn giản hóa quy trình đặt lịch hẹn của bạn.\n\n" +
                "2. Đặt lịch hẹn đầu tiên: Duyệt qua danh sách nha sĩ có sẵn và tìm thời gian phù hợp với lịch trình của bạn. Bạn có thể dễ dàng đặt lịch hẹn trực tuyến hoặc bằng cách gọi điện đến trung tâm chăm sóc khách hàng của chúng tôi.\n\n" +
                "3. Khám phá các nguồn tài nguyên của chúng tôi: Chúng tôi cung cấp nhiều nguồn tài nguyên hữu ích để hỗ trợ hành trình sức khỏe răng miệng của bạn, bao gồm các bài viết, video và mẹo về cách duy trì vệ sinh răng miệng tốt.\n\n" +
                "Chúng tôi luôn sẵn sàng hỗ trợ:\n\n" +
                "Nếu bạn có bất kỳ câu hỏi nào hoặc cần trợ giúp về tài khoản của mình, vui lòng liên hệ với đội ngũ dịch vụ khách hàng thân thiện của chúng tôi. Chúng tôi luôn sẵn lòng giúp đỡ!\n\n" +
                "Cảm ơn bạn đã chọn " + "F-Dental" + "!\n\n" +
                "Trân trọng,\n\n" +
                "Đội ngũ F-Dental";
        message.setText(body);

        // Send the email (assuming you have a mailSender bean configured)
        mailSender.send(message);
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
        }
        return res;
    }

    @Override
    public boolean isUsernameOrEmailExisted(String username, String email) {
        return customerRepository.existsByUsernameOrEmail(username, email) ||
                dentistRepository.existsByEmailOrUsername(username, email) ||
                staffRepository.existsByEmailOrUsername(username, email) ||
                ownerRepository.existsByEmailOrUsername(username, email);
    }

}
