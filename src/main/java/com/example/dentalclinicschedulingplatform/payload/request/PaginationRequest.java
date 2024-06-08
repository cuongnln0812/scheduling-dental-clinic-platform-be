package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {
    @NotNull(message = "Page cannot be null")
    @Positive(message = "Page must be a positive number")
    private int page;
    @NotNull(message = "Size cannot be null")
    @Positive(message = "Size price must be a positive number")
    private int size;
}
