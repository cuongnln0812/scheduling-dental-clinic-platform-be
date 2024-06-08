package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateRequest {
    @NotNull(message = "Category Id can not be null")
    public Long categoryId;

    @NotBlank(message = "Category name can not be null")
    public String categoryName;

    @NotNull(message = "Category status can not be null")
    public boolean categoryStatus;
}
