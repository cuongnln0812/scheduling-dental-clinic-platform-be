package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.BlogCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.BlogUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.BranchCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.service.IBlogService;
import com.example.dentalclinicschedulingplatform.service.IBranchService;
import com.example.dentalclinicschedulingplatform.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Tag(name = "Blog Controller")
@RequestMapping("/api/v1/blog")
@Validated
@Valid
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BlogController {
    private final IBlogService blogService;

    @Operation(
            summary = "Create blog"
    )
    @PreAuthorize("hasAnyRole('STAFF')")
    @PostMapping()
    public ResponseEntity<ApiResponse<BlogDetailResponse>> createBlog(@Valid @RequestBody BlogCreateRequest request) {
        log.info("Has request with data: {}", request.toString());
        BlogDetailResponse newBlog = blogService.createBlog(request);
        ApiResponse<BlogDetailResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Create blog successfully",
                newBlog);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Approve blog"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogDetailResponse>> approveBlog(@PathVariable("id") Long id,
                                                                       @RequestParam boolean isApproved) {
        log.info("Has request with data: {}", id.toString());
        ApiResponse<BlogDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Approve blog successfully",
                blogService.approveBlog(id, isApproved));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "View detail blog"
    )
    //@PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'STAFF', 'CUSTOMER', 'DENTIST')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogDetailResponse>> viewBlog(@PathVariable("id") Long id) {
        log.info("Has request with data: {}", id.toString());
        ApiResponse<BlogDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "View detail blog successfully",
                blogService.viewBlog(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all active blog"
    )
    //@PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<BlogDetailResponse>>> getAllActiveBlog(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size) {
        ApiResponse<Page<BlogDetailResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get all active blog successfully!",
                blogService.getAllActiveBlog(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all active and inactive blog"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<BlogDetailResponse>>> getAllActiveAndInactiveBlog(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size) {
        ApiResponse<Page<BlogDetailResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get all active and inactive blog successfully!",
                blogService.getAllActiveAndInactiveBlog(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all pending blog"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<BlogDetailResponse>>> getAllPendingBlog(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size) {
        ApiResponse<Page<BlogDetailResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get all pending blog successfully!",
                blogService.getAllPendingBlog(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all blog by clinic id"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/clinic/{id}")
    public ResponseEntity<ApiResponse<Page<BlogDetailResponse>>> getAllBlogByClinic(
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size) {
        ApiResponse<Page<BlogDetailResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get all blog by clinic id successfully!",
                blogService.getAllBlogByClinicId(id, page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Update blog"
    )
    @PreAuthorize("hasAnyRole('STAFF')")
    @PutMapping()
    public ResponseEntity<ApiResponse<BlogDetailResponse>> updateBlog(@Valid @RequestBody BlogUpdateRequest request) {
        log.info("Has request with data: {}", request.toString());
        BlogDetailResponse blog = blogService.updateBlog(request);
        ApiResponse<BlogDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Update blog successfully",
                blog);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete blog"
    )
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogDetailResponse>> deleteBlog(@PathVariable("id") Long id) {
        log.info("Has request with data: {}", id.toString());
        BlogDetailResponse blog = blogService.removeBlog(id);
        ApiResponse<BlogDetailResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Change status blog successfully",
                blog);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
