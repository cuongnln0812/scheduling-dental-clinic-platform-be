package com.example.dentalclinicschedulingplatform.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Summary is required")
    private String summary;
    @NotBlank(message = "Content is required")
    private String content;
    @NotBlank(message = "Thumbnail is required")
    private String thumbnail;
}
