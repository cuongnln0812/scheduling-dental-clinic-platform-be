package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.Customer;
import com.example.dentalclinicschedulingplatform.entity.Feedback;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.SendFeedbackRequest;
import com.example.dentalclinicschedulingplatform.payload.response.SendFeedbackResponse;
import com.example.dentalclinicschedulingplatform.payload.response.StarRatingResponse;
import com.example.dentalclinicschedulingplatform.payload.response.SummaryFeedbackResponse;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.repository.FeedbackRepository;
import com.example.dentalclinicschedulingplatform.repository.ClinicRepository;
import com.example.dentalclinicschedulingplatform.service.IFeedbackService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class FeedbackService implements IFeedbackService {

    private final ModelMapper modelMapper;
    private final FeedbackRepository feedbackRepository;
    private final CustomerRepository customerRepository;
    private final ClinicBranchRepository clinicBranchRepository;
    private final ClinicRepository clinicRepository;

    @Override
    public SendFeedbackResponse sendFeedback(SendFeedbackRequest request) {
        String username = getCurrentUsername();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));
        Feedback feedback = new Feedback();
        feedback.setComment(request.getComment());
        feedback.setRating(request.getRating());
        feedback.setCustomer(customer);
        ClinicBranch clinicBranch = clinicBranchRepository.findById(request.getBranchclinicId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch Clinic not found"));
        feedback.setClinicBranch(clinicBranch);
        feedback = feedbackRepository.save(feedback);

        SendFeedbackResponse response = modelMapper.map(feedback, SendFeedbackResponse.class);
        response.setBranchCity(clinicBranch.getCity());
        response.setCustomerAvatar(customer.getAvatar());

        return response;
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
    public SummaryFeedbackResponse getFeedbackByBranchId(Long branchId) {
        List<Feedback> feedbackList = feedbackRepository.findByClinicBranch_BranchId(branchId);
        return createSummaryFeedbackResponse(feedbackList);
    }

    @Override
    public SummaryFeedbackResponse getFeedbackByClinicId(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));
        List<Feedback> feedbackList = clinic.getClinicBranch().stream()
                .flatMap(branch -> branch.getFeedbacks().stream())
                .collect(Collectors.toList());
        return createSummaryFeedbackResponse(feedbackList);
    }

    @Override
    public SummaryFeedbackResponse getAllFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll();
        return createSummaryFeedbackResponse(feedbackList);
    }

    private SummaryFeedbackResponse createSummaryFeedbackResponse(List<Feedback> feedbackList) {
        double averageRating = feedbackList.stream()
                .mapToDouble(Feedback::getRating)
                .average()
                .orElse(0.0);
        long totalFeedback = feedbackList.size();

        Map<Integer, Long> starCounts = feedbackList.stream()
                .collect(Collectors.groupingBy(feedback -> (int) feedback.getRating(), Collectors.counting()));

        List<StarRatingResponse> starRatings = IntStream.rangeClosed(1, 5)
                .mapToObj(star -> new StarRatingResponse(star, starCounts.getOrDefault(star, 0L)))
                .collect(Collectors.toList());

        List<SendFeedbackResponse> feedbackResponses = feedbackList.stream()
                .map(feedback -> {
                    SendFeedbackResponse response = modelMapper.map(feedback, SendFeedbackResponse.class);
                    response.setBranchCity(feedback.getClinicBranch().getCity());
                    response.setCustomerAvatar(feedback.getCustomer().getAvatar());
                    return response;
                })
                .collect(Collectors.toList());

        return new SummaryFeedbackResponse(feedbackResponses, averageRating, totalFeedback, starRatings);
    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Feedback not found"));
        feedbackRepository.delete(feedback);
    }
}
