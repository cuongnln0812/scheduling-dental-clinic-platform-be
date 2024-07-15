package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.Customer;
import com.example.dentalclinicschedulingplatform.entity.Feedback;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.SendFeedbackRequest;
import com.example.dentalclinicschedulingplatform.payload.response.SendFeedbackResponse;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.ClinicRepository;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.repository.FeedbackRepository;
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
        ClinicBranch clinicBranch = clinicBranchRepository.findById(request.getBranchclinicID())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch Clinic not found"));
        feedback.setClinicBranch(clinicBranch);
        feedback = feedbackRepository.save(feedback);
        SendFeedbackResponse response = modelMapper.map(feedback, SendFeedbackResponse.class);
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
    public List<SendFeedbackResponse> getFeedbackByBranchId(Long branchId) {
        List<Feedback> feedbackList = feedbackRepository.findByClinicBranch_BranchId(branchId);

        double averageRating = feedbackList.stream()
                .mapToDouble(Feedback::getRating)
                .average()
                .orElse(0.0);
        long totalFeedback = feedbackList.size();

        Map<Integer, Long> starCounts = feedbackList.stream()
                .collect(Collectors.groupingBy(feedback -> (int) feedback.getRating(), Collectors.counting()));

        long totalOneStar = starCounts.getOrDefault(1, 0L);
        long totalTwoStar = starCounts.getOrDefault(2, 0L);
        long totalThreeStar = starCounts.getOrDefault(3, 0L);
        long totalFourStar = starCounts.getOrDefault(4, 0L);
        long totalFiveStar = starCounts.getOrDefault(5, 0L);

        return feedbackList.stream()
                .map(feedback -> {
                    SendFeedbackResponse response = modelMapper.map(feedback, SendFeedbackResponse.class);
                    response.setAverageRating(averageRating);
                    response.setTotalFeedback(totalFeedback);
                    response.setTotalOneStar(totalOneStar);
                    response.setTotalTwoStar(totalTwoStar);
                    response.setTotalThreeStar(totalThreeStar);
                    response.setTotalFourStar(totalFourStar);
                    response.setTotalFiveStar(totalFiveStar);
                    return response;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<SendFeedbackResponse> getFeedbackByClinicId(Long clinicId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));
        List<Feedback> feedbackList = clinic.getClinicBranch().stream()
                .flatMap(branch -> branch.getFeedbacks().stream())
                .collect(Collectors.toList());

        double averageRating = feedbackList.stream()
                .mapToDouble(Feedback::getRating)
                .average()
                .orElse(0.0);
        long totalFeedback = feedbackList.size();

        Map<Integer, Long> starCounts = feedbackList.stream()
                .collect(Collectors.groupingBy(feedback -> (int) feedback.getRating(), Collectors.counting()));

        long totalOneStar = starCounts.getOrDefault(1, 0L);
        long totalTwoStar = starCounts.getOrDefault(2, 0L);
        long totalThreeStar = starCounts.getOrDefault(3, 0L);
        long totalFourStar = starCounts.getOrDefault(4, 0L);
        long totalFiveStar = starCounts.getOrDefault(5, 0L);

        return feedbackList.stream()
                .map(feedback -> {
                    SendFeedbackResponse response = modelMapper.map(feedback, SendFeedbackResponse.class);
                    response.setAverageRating(averageRating);
                    response.setTotalFeedback(totalFeedback);
                    response.setTotalOneStar(totalOneStar);
                    response.setTotalTwoStar(totalTwoStar);
                    response.setTotalThreeStar(totalThreeStar);
                    response.setTotalFourStar(totalFourStar);
                    response.setTotalFiveStar(totalFiveStar);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SendFeedbackResponse> getAllFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll();
        double averageRating = feedbackList.stream()
                .mapToDouble(Feedback::getRating)
                .average()
                .orElse(0.0);
        long totalFeedback = feedbackList.size();

        Map<Integer, Long> starCounts = feedbackList.stream()
                .collect(Collectors.groupingBy(feedback -> (int) feedback.getRating(), Collectors.counting()));

        long totalOneStar = starCounts.getOrDefault(1, 0L);
        long totalTwoStar = starCounts.getOrDefault(2, 0L);
        long totalThreeStar = starCounts.getOrDefault(3, 0L);
        long totalFourStar = starCounts.getOrDefault(4, 0L);
        long totalFiveStar = starCounts.getOrDefault(5, 0L);

        return feedbackList.stream()
                .map(feedback -> {
                    SendFeedbackResponse response = modelMapper.map(feedback, SendFeedbackResponse.class);
                    response.setAverageRating(averageRating);
                    response.setTotalFeedback(totalFeedback);
                    response.setTotalOneStar(totalOneStar);
                    response.setTotalTwoStar(totalTwoStar);
                    response.setTotalThreeStar(totalThreeStar);
                    response.setTotalFourStar(totalFourStar);
                    response.setTotalFiveStar(totalFiveStar);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Feedback not found"));
        feedbackRepository.delete(feedback);
    }
}
