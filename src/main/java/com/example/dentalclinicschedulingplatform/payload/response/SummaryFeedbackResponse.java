package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryFeedbackResponse {
    private List<SendFeedbackResponse> feedbacks;
    private double averageRating;
    private long totalFeedback;
    private List<StarRatingResponse> starRatings;
}
