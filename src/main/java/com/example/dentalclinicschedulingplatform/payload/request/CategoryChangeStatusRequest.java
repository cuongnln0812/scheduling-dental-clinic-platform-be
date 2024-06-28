package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryChangeStatusRequest {
    @NotNull(message = "Category Id can not be null")
    public Long categoryId;

    @NotNull(message = "Category status can not be null")
    public boolean categoryStatus;
}
