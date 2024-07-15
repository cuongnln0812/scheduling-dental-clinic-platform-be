package com.example.dentalclinicschedulingplatform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendFeedbackResponse {
    private Long feedbackId;
    private String comment;
    private double rating;
    private String customerFullName;
    private String branchName;
    private double averageRating;
    private long totalFeedback;
    private long totalOneStar;
    private long totalTwoStar;
    private long totalThreeStar;
    private long totalFourStar;
    private long totalFiveStar;

}
