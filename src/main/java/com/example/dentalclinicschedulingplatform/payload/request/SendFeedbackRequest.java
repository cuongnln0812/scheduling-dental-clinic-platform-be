package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendFeedbackRequest {
    @NotBlank(message = "Comment cannot be null")
    private String comment;

    @Size(min = 1, max = 5, message = "Rating must be between 1 and 5 digits")
    @NotNull(message = "Rating cannot be null")
    private int rating;

    @NotBlank(message = "BranchClinicID cannot be null")
    private long branchclinicID;

    @NotBlank(message = "UserID cannot be null")
    private long userID;


}
