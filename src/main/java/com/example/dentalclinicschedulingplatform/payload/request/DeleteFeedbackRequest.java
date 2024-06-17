package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFeedbackRequest {
    @NotNull(message = "feedbackID cannot be null.")
    private Long feedbackID;
}
