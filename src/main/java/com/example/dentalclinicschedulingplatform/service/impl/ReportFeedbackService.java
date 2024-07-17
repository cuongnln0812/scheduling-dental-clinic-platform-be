package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Feedback;
import com.example.dentalclinicschedulingplatform.entity.Report;
import com.example.dentalclinicschedulingplatform.entity.ReportReason;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.CreateReportRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ReportResponse;
import com.example.dentalclinicschedulingplatform.repository.FeedbackRepository;
import com.example.dentalclinicschedulingplatform.repository.ReportRepository;
import com.example.dentalclinicschedulingplatform.service.IReportFeedbackService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReportFeedbackService implements IReportFeedbackService {

    private final ReportRepository reportRepository;
    private final FeedbackRepository feedbackRepository;
    private final ModelMapper modelMapper;

    @Override
    public ReportResponse createReport(CreateReportRequest request) {
        Feedback feedback = feedbackRepository.findById(request.getFeedbackId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Feedback not found"));
        String reporter = getCurrentUsername();

        if (reporter.equals(request.getReportedCustomer())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "You cannot report yourself.");
        }

        reportRepository.findByFeedback_FeedbackIdAndReporter(request.getFeedbackId(), reporter)
                .ifPresent(existingReport -> {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "You have already reported this feedback.");
                });
        List<ReportReason> reportReasons = request.getReportReason();
        Report report = new Report();
        report.setFeedback(feedback);
        report.setReportReasons(reportReasons);
        report.setReporter(reporter);
        report.setReportedCustomer(request.getReportedCustomer());
        report.setCreatedDateTime(LocalDateTime.now());
        report.setClinic(feedback.getClinicBranch().getClinic());
        report.setBranch(feedback.getClinicBranch());
        report = reportRepository.save(report);
        return convertToReportResponse(report);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    @Override
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::convertToReportResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportResponse> getReportsByClinicId(Long clinicId) {
        return reportRepository.findByClinic_ClinicId(clinicId).stream()
                .map(this::convertToReportResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void approveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));

        Feedback feedback = report.getFeedback();
        feedbackRepository.delete(feedback);
        reportRepository.delete(report);
    }

    @Override
    public void declineReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));
        reportRepository.delete(report);
    }

    private ReportResponse convertToReportResponse(Report report) {
        ReportResponse response = modelMapper.map(report, ReportResponse.class);
        Feedback feedback = report.getFeedback();
        response.setReportReasons(
                String.join(", ",
                        report.getReportReasons().stream()
                                .map(Enum::name)
                                .collect(Collectors.toList())
                )
        );
        response.setAvatar(feedback.getCustomer().getAvatar());
        response.setFullName(feedback.getCustomer().getFullName());
        response.setBranchName(feedback.getClinicBranch().getBranchName());
        response.setCity(feedback.getClinicBranch().getCity());
        response.setComment(feedback.getComment());
        response.setRating(Double.valueOf(feedback.getRating()));
        Long clinicId = feedback.getClinicBranch().getClinic().getClinicId();
        List<Feedback> feedbacks = feedbackRepository.findByClinicBranch_Clinic_ClinicId(clinicId);
        double averageRating = feedbacks.stream()
                .mapToDouble(Feedback::getRating)
                .average()
                .orElse(0.0);
        long totalFeedback = feedbacks.size();
        response.setAverageRating(averageRating);
        response.setTotalFeedback(totalFeedback);
        return response;
    }
}
