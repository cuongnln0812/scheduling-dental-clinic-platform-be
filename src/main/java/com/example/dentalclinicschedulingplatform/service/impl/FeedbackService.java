package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.ClinicBranch;
import com.example.dentalclinicschedulingplatform.entity.Customer;
import com.example.dentalclinicschedulingplatform.entity.Feedback;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.SendFeedbackRequest;
import com.example.dentalclinicschedulingplatform.payload.response.SendFeedbackResponse;
import com.example.dentalclinicschedulingplatform.repository.ClinicBranchRepository;
import com.example.dentalclinicschedulingplatform.repository.CustomerRepository;
import com.example.dentalclinicschedulingplatform.repository.FeedbackRepository;
import com.example.dentalclinicschedulingplatform.service.IFeedbackService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService implements IFeedbackService {

    private final ModelMapper modelMapper;
    private final FeedbackRepository feedbackRepository;
    private final CustomerRepository customerRepository;
    private final ClinicBranchRepository clinicBranchRepository;

    @Override
    public SendFeedbackResponse sendFeedback(SendFeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setComment(request.getComment());
        feedback.setRating(request.getRating());
        feedback.setCreatedDateTime();
        Customer customer = customerRepository.findById(request.getUserID())
                .orElseThrow(() -> new ApiException ( HttpStatus.NOT_FOUND ,"Customer not found" ));
        feedback.setCustomer(customer);

        ClinicBranch clinicBranch = clinicBranchRepository.findById(request.getBranchclinicID())
                .orElseThrow(() -> new ApiException ( HttpStatus.NOT_FOUND ,"Branch Clinic not found" ));
        feedback.setClinicBranch(clinicBranch);
        feedback = feedbackRepository.save(feedback);
        SendFeedbackResponse response = modelMapper.map (feedback, SendFeedbackResponse.class);
        return response;
    }

    @Override
    public List<SendFeedbackResponse> getFeedbackByBranchId(Long branchId) {
        List<Feedback> feedbackList = feedbackRepository.findByClinicBranch_BranchId(branchId);
        return feedbackList.stream()
                .map(feedback -> modelMapper.map(feedback, SendFeedbackResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SendFeedbackResponse> getAllFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll();
        return feedbackList.stream()
                .map(feedback -> modelMapper.map(feedback, SendFeedbackResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Feedback not found"));
        feedbackRepository.delete(feedback);
    }
}
