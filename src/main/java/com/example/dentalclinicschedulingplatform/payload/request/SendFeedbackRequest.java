package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.*;
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

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @NotNull(message = "Rating cannot be null")
    private Integer rating;

    @NotNull(message = "BranchClinicID cannot be null")
    private long branchclinicId;
}
