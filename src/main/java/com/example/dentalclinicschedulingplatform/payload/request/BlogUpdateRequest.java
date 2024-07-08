package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogUpdateRequest {
    @NotNull(message = "Blog Id can not be null")
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String thumbnail;
}

