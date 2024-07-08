package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.service.IMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService implements IMailService {

    private final JavaMailSender mailSender;
//    private final ConcurrentHashMap<String, Long> emailSendTimestamps = new ConcurrentHashMap<>();
//    private final long emailCooldown = TimeUnit.MINUTES.toMillis(1); // 1 minutes cooldown
//    private boolean canSendEmail(String email) {
//        long now = System.currentTimeMillis();
//        return emailSendTimestamps.getOrDefault(email, 0L) + emailCooldown <= now;
//    }
//
//    private void updateEmailTimestamp(String email) {
//        emailSendTimestamps.put(email, System.currentTimeMillis());
//    }

    @Async
    @Override
    public void sendCustomerRegistrationMail(Customer user){
//        if (!canSendEmail(user.getEmail())) {
//            System.out.println("Please wait " emailCooldown "for: " + user.getEmail());
//        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        message.setTo(user.getEmail());
        // Set a meaningful message
        message.setSubject("[F-Dental] - Tài khoản được tạo thành công");
        String body = "Kính gửi " + user.getFullName() + ",\n\n" +
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
    }

    @Async
    @Override
    public void sendClinicRequestConfirmationMail(String fullName, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        message.setTo(email);
        // Set a meaningful message
        message.setSubject("[F-Dental] - Đơn đăng kí làm đối tác của bạn đã được tiếp nhận");
        String body = "Kính gửi " + fullName + ",\n\n" +
                "Chúng tôi đã nhận được đơn đăng kí làm đối tác của bạn tại F-Dental. Đơn đăng kí của bạn hiện đang được xem xét và chúng tôi sẽ thông báo cho bạn khi có kết quả.\n\n" +
                "Chúng tôi sẽ cố gắng hoàn thành quá trình duyệt đơn trong thời gian sớm nhất. Nếu bạn có bất kỳ câu hỏi nào, xin vui lòng liên hệ với đội ngũ dịch vụ khách hàng của chúng tôi qua email hoặc điện thoại.\n\n" +
                "Cảm ơn bạn đã chọn F-Dental và hy vọng sẽ sớm được hợp tác cùng bạn!\n\n" +
                "Trân trọng,\n\n" +
                "Đội ngũ F-Dental";
        message.setText(body);
        // Send the email (assuming you have a mailSender bean configured)
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendClinicRequestApprovalMail(ClinicOwner owner, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        message.setTo(owner.getEmail());
        // Set a meaningful message
        message.setSubject("[F-Dental] - Đơn đăng kí làm đối tác của bạn đã được XÁC NHẬN");
        String body = "Kính gửi " + owner.getFullName() + ",\n\n" +
                "Chúng tôi rất vui mừng thông báo rằng đơn đăng kí làm đối tác của bạn tại F-Dental đã được XÁC NHẬN.\n\n" +
                "Dưới đây là thông tin tài khoản để bạn đăng nhập vào hệ thống:\n\n" +
                "Tên đăng nhập: " + owner.getUsername() + "\n" +
                "Mật khẩu tạm thời: " + password + "\n\n" +
                "Để bảo mật tài khoản của bạn, vui lòng đăng nhập và đổi mật khẩu ngay sau khi nhận được email này.\n\n" +
                "Sau khi đăng nhập, vui lòng cung cấp đầy đủ thông tin cá nhân và chi tiết bảo hiểm nha khoa của bạn trong cài đặt tài khoản. Điều này sẽ giúp chúng tôi hoàn tất quá trình đưa bạn lên nền tảng và bắt đầu sử dụng dịch vụ.\n\n" +
                "Chúng tôi luôn sẵn sàng hỗ trợ:\n\n" +
                "Nếu bạn có bất kỳ câu hỏi nào hoặc cần trợ giúp, vui lòng liên hệ với đội ngũ dịch vụ khách hàng thân thiện của chúng tôi. Chúng tôi luôn sẵn lòng giúp đỡ!\n\n" +
                "Cảm ơn bạn đã chọn F-Dental và chúng tôi rất mong được hợp tác cùng bạn!\n\n" +
                "Trân trọng,\n\n" +
                "Đội ngũ F-Dental";
        message.setText(body);
        // Send the email (assuming you have a mailSender bean configured)
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendClinicRequestRejectionMail(String fullName, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        message.setTo(email);
        // Set a meaningful message
        message.setSubject("[F-Dental] - Đơn đăng kí làm đối tác của bạn đã bị TỪ CHỐI");
        String body = "Kính gửi " + fullName + ",\n\n" +
                "Chúng tôi rất tiếc phải thông báo rằng đơn đăng kí làm đối tác của bạn tại F-Dental đã bị từ chối sau khi xem xét kỹ lưỡng.\n\n" +
                "Chúng tôi hiểu rằng đây không phải là tin tức mà bạn mong đợi và xin chân thành xin lỗi vì sự bất tiện này. Đơn đăng kí của bạn không đáp ứng được một số tiêu chí mà chúng tôi đặt ra cho đối tác của mình.\n\n" +
                "Nếu bạn có bất kỳ câu hỏi nào về lý do từ chối hoặc cần thêm thông tin, xin vui lòng liên hệ với đội ngũ dịch vụ khách hàng của chúng tôi qua email hoặc điện thoại. Chúng tôi sẵn sàng giải đáp mọi thắc mắc và hỗ trợ bạn trong quá trình này.\n\n" +
                "Cảm ơn bạn đã quan tâm đến việc trở thành đối tác của F-Dental. Chúng tôi hy vọng sẽ có cơ hội hợp tác cùng bạn trong tương lai.\n\n" +
                "Trân trọng,\n\n" +
                "Đội ngũ F-Dental";
        message.setText(body);
        // Send the email (assuming you have a mailSender bean configured)
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendStaffRequestApprovalMail(ClinicStaff staff, String branchName, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        message.setTo(staff.getEmail());
        // Set a meaningful message
        message.setSubject("[F-Dental] - Đơn đăng kí làm nhân viên của bạn đã được XÁC NHẬN");
        String body = "Kính gửi " + staff.getFullName() + ",\n\n" +
                      "Chúng tôi rất vui mừng thông báo rằng đơn đăng kí làm nhân viên của bạn tại " + branchName + " đã được XÁC NHẬN.\n\n" +
                      "Dưới đây là thông tin tài khoản để bạn đăng nhập vào hệ thống:\n\n" +
                      "Tên đăng nhập: " + staff.getUsername() + "\n" +
                      "Mật khẩu tạm thời: " + password + "\n\n" +
                      "Để bảo mật tài khoản của bạn, vui lòng đăng nhập và đổi mật khẩu ngay sau khi nhận được email này.\n\n" +
                      "Nếu bạn có bất kỳ câu hỏi nào hoặc cần trợ giúp, vui lòng liên hệ với đội ngũ dịch vụ khách hàng thân thiện của chúng tôi. Chúng tôi luôn sẵn lòng giúp đỡ!\n\n" +
                      "Cảm ơn bạn đã chọn F-Dental và chúng tôi rất mong được hợp tác cùng bạn!\n\n" +
                      "Trân trọng,\n\n" +
                      "Đội ngũ F-Dental";
        message.setText(body);
        // Send the email (assuming you have a mailSender bean configured)
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendStaffRequestRejectionMail(ClinicStaff staff, String branchName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        message.setTo(staff.getEmail());
        // Set a meaningful message
        message.setSubject("[F-Dental] - Đơn đăng kí làm nhân viên của bạn đã bị TỪ CHỐI");
        String body = "Kính gửi " + staff.getFullName() + ",\n\n" +
                      "Chúng tôi rất tiếc phải thông báo rằng đơn đăng kí làm nhân viên của bạn tại " + branchName + " đã bị từ chối sau khi xem xét kỹ lưỡng.\n\n" +
                      "Chúng tôi hiểu rằng đây không phải là tin tức mà bạn mong đợi và xin chân thành xin lỗi vì sự bất tiện này. Đơn đăng kí của bạn không đáp ứng được một số tiêu chí mà chúng tôi đã đặt ra.\n\n" +
                      "Nếu bạn có bất kỳ câu hỏi nào về lý do từ chối hoặc cần thêm thông tin, xin vui lòng liên hệ với đội ngũ dịch vụ khách hàng của chúng tôi qua email hoặc điện thoại. Chúng tôi sẵn sàng giải đáp mọi thắc mắc và hỗ trợ bạn trong quá trình này.\n\n" +
                      "Trân trọng,\n\n" +
                      "Đội ngũ F-Dental";
        message.setText(body);
        // Send the email (assuming you have a mailSender bean configured)
        mailSender.send(message);
    }

    @Async
    @Override
    public void senDentistRequestConfirmationMail(Dentist dentist, ClinicOwner owner) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        message.setTo(owner.getEmail());
        message.setSubject("[F-Dental] - Đơn đăng kí tài khoản nha sĩ đang chờ duyệt");

        String body = "Kính gửi " + owner.getFullName() + ",\n\n" +
                "Chúng tôi xin thông báo rằng tài khoản nha sĩ cho " + dentist.getFullName() + " đã được tiếp nhận và hiện đang chờ duyệt.\n\n" +
                "Vui lòng lưu ý rằng tài khoản này sẽ chỉ được kích hoạt sau khi quá trình phê duyệt hoàn tất. Chúng tôi sẽ gửi thông báo và thông tin đăng nhập qua email của nha sĩ ngay khi tài khoản được phê duyệt.\n\n" +
                "Nếu có bất kỳ câu hỏi nào hoặc cần trợ giúp, xin vui lòng liên hệ với đội ngũ dịch vụ khách hàng của chúng tôi qua email hoặc điện thoại. Chúng tôi luôn sẵn lòng giúp đỡ!\n\n" +
                "Cảm ơn bạn đã chọn F-Dental và hy vọng sẽ sớm được hợp tác cùng bạn!\n\n" +
                "Trân trọng,\n\n" +
                "Đội ngũ F-Dental";

        message.setText(body);
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendDentistRequestApprovalMail(Dentist dentist, String password, ClinicOwner owner) {
        SimpleMailMessage dentistMessage = new SimpleMailMessage();
        dentistMessage.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        dentistMessage.setTo(dentist.getEmail());
        // Set a meaningful message
        dentistMessage.setSubject("[F-Dental] - Tài khoản được duyệt và tạo thành công");
        dentistMessage.setText("Hi, " + dentist.getFullName() + ",\n\n" +
                "Tài khoản đăng nhập vào hệ thống F-Dental của bạn đã được duyệt và tạo thành công.\n" +
                "Vui lòng truy cập hệ thống theo thông tin sau:\n" +
                "• Username: " + dentist.getUsername() + "\n" +
                "• Password: " + password + "\n" +
                "Lưu ý: Vui lòng thay đổi mật khẩu sau khi đăng nhập.\n");
        mailSender.send(dentistMessage);

        SimpleMailMessage ownerMessage = new SimpleMailMessage();
        ownerMessage.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        ownerMessage.setTo(owner.getEmail());
        ownerMessage.setSubject("[F-Dental] - Đơn đăng kí tài khoản nha sĩ đã được duyệt");
        String body = "Kính gửi " + owner.getFullName() + ",\n\n" +
                "Chúng tôi rất vui mừng thông báo rằng đơn đăng kí tài khoản nha sĩ của " + dentist.getFullName() + " đã được duyệt.\n\n" +
                "Dưới đây là thông tin tài khoản để nha sĩ đăng nhập vào hệ thống:\n\n" +
                "Tên đăng nhập: " + dentist.getUsername() + "\n\n" +
                "Để bảo mật tài khoản, nha sĩ cần đăng nhập và đổi mật khẩu ngay sau khi nhận được email này.\n\n" +
                "Nếu có bất kỳ câu hỏi nào hoặc cần trợ giúp, vui lòng liên hệ với đội ngũ dịch vụ khách hàng thân thiện của chúng tôi. Chúng tôi luôn sẵn lòng giúp đỡ!\n\n" +
                "Cảm ơn bạn đã chọn F-Dental và chúng tôi rất mong được hợp tác cùng bạn!\n\n" +
                "Trân trọng,\n\n" +
                "Đội ngũ F-Dental";
        ownerMessage.setText(body);
        mailSender.send(ownerMessage);
    }

    @Async
    @Override
    public void sendCustomerAppointmentRequestConfirmationMail(Customer customer, Appointment appointment) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        message.setTo(appointment.getCustomerEmail());
        message.setSubject("[F-Dental] - Xác nhận lịch hẹn thành công");
        message.setText("Chào " + appointment.getCustomerName() + ",\n\n" +
                "Cảm ơn bạn đã đặt lịch hẹn với F-Dental. Chúng tôi rất vui thông báo rằng lịch hẹn của bạn đã được xác nhận. Dưới đây là thông tin chi tiết về lịch hẹn của bạn:\n\n" +
                "• Ngày hẹn: " + appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "• Giờ hẹn: " + appointment.getSlot().getStartTime() + " - " + appointment.getSlot().getEndTime() + "\n" +
                "• Nha sĩ: " + appointment.getDentist().getFullName() + "\n" +
                "• Dịch vụ: " + appointment.getService().getServiceName() + "\n" +
                "• Địa chỉ: " + appointment.getClinicBranch().getAddress() + "\n\n" +
                "Vui lòng có mặt đúng giờ và mang theo tất cả các giấy tờ cần thiết. Nếu bạn có bất kỳ câu hỏi nào hoặc cần thay đổi lịch hẹn, xin vui lòng liên hệ với chúng tôi qua số điện thoại hoặc email này.\n\n" +
                "Cảm ơn bạn đã tin tưởng và lựa chọn F-Dental. Chúng tôi rất mong được phục vụ bạn.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ F-Dental");
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendCustomerAppointmentCancelConfirmationMail(Customer customer, Appointment appointment, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
        message.setTo(appointment.getCustomerEmail());
        message.setSubject("[F-Dental] - Xác nhận hủy lịch hẹn");
        message.setText("Chào " + appointment.getCustomerName() + ",\n\n" +
                "Chúng tôi xin thông báo rằng lịch hẹn của bạn tại F-Dental đã được hủy thành công. Dưới đây là thông tin chi tiết về lịch hẹn đã hủy:\n\n" +
                "• Ngày hẹn: " + appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "• Giờ hẹn: " + appointment.getSlot().getStartTime() + " - " + appointment.getSlot().getEndTime() + "\n" +
                "• Nha sĩ: " + appointment.getDentist().getFullName() + "\n" +
                "• Dịch vụ: " + appointment.getService().getServiceName() + "\n" +
                "• Địa chỉ: " + appointment.getClinicBranch().getAddress() + "\n\n" +
                "Lý do hủy: " + reason + "\n\n" +
                "Nếu bạn có bất kỳ câu hỏi nào hoặc cần đặt lại lịch hẹn, xin vui lòng liên hệ với chúng tôi qua số điện thoại hoặc email này.\n\n" +
                "Cảm ơn bạn đã tin tưởng và lựa chọn F-Dental. Chúng tôi rất mong được phục vụ bạn trong tương lai.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ F-Dental");
        mailSender.send(message);
    }

//    @Async
//    @Override
//    public void sendBranchRequestApprovalMail(ClinicOwner owner) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
//        message.setTo(owner.getEmail());
//        // Set a meaningful message
//        message.setSubject("[F-Dental] - Đơn đăng kí chi nhánh mới của bạn đã được XÁC NHẬN");
//        String body = "Kính gửi " + owner.getFullName() + ",\n\n" +
//                      "Chúng tôi rất vui mừng thông báo rằng đơn đăng kí chi nhánh mới của bạn tại F-Dental đã được XÁC NHẬN.\n\n" +
//                      "Vui lòng cập nhật đầy đủ thông tin của chi nhánh mới và chi tiết bảo hiểm nha khoa của chi nhánh mới. Điều này sẽ giúp chúng tôi hoàn tất quá trình đưa chi nhánh của bạn lên nền tảng.\n\n" +
//                      "Chúng tôi luôn sẵn sàng hỗ trợ:\n\n" +
//                      "Nếu bạn có bất kỳ câu hỏi nào hoặc cần trợ giúp, vui lòng liên hệ với đội ngũ dịch vụ khách hàng thân thiện của chúng tôi. Chúng tôi luôn sẵn lòng giúp đỡ!\n\n" +
//                      "Cảm ơn bạn đã chọn F-Dental và chúng tôi rất mong được hợp tác cùng bạn!\n\n" +
//                      "Trân trọng,\n\n" +
//                      "Đội ngũ F-Dental";
//        message.setText(body);
//        // Send the email (assuming you have a mailSender bean configured)
//        mailSender.send(message);
//    }
//
//    @Async
//    @Override
//    public void sendBranchRequestRejectionMail(ClinicOwner owner) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("\"F-Dental\" <fdental.automatic.noreply@gmail.com>");
//        message.setTo(owner.getEmail());
//        // Set a meaningful message
//        message.setSubject("[F-Dental] - Đơn đăng kí chi nhánh mới của bạn đã bị TỪ CHỐI");
//        String body = "Kính gửi " + owner.getFullName() + ",\n\n" +
//                      "Chúng tôi rất tiếc phải thông báo rằng đơn đăng kí chi nhánh mới của bạn tại F-Dental đã bị từ chối sau khi xem xét kỹ lưỡng.\n\n" +
//                      "Chúng tôi hiểu rằng đây không phải là tin tức mà bạn mong đợi và xin chân thành xin lỗi vì sự bất tiện này. Đơn đăng kí của bạn không đáp ứng được một số tiêu chí mà chúng tôi đặt ra cho đối tác của mình.\n\n" +
//                      "Nếu bạn có bất kỳ câu hỏi nào về lý do từ chối hoặc cần thêm thông tin, xin vui lòng liên hệ với đội ngũ dịch vụ khách hàng của chúng tôi qua email hoặc điện thoại. Chúng tôi sẵn sàng giải đáp mọi thắc mắc và hỗ trợ bạn trong quá trình này.\n\n" +
//                      "Trân trọng,\n\n" +
//                      "Đội ngũ F-Dental";
//        message.setText(body);
//        // Send the email (assuming you have a mailSender bean configured)
//        mailSender.send(message);
//    }
}
