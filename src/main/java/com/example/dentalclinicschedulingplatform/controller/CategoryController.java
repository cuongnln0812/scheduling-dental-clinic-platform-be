package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.CategoryCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CategoryUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.service.impl.AuthenticateService;
import com.example.dentalclinicschedulingplatform.service.impl.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuthenticateService authenticationService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<CategoryViewResponse>>> viewCategoryListByClinic
            (@RequestParam Long clinicId,
             @RequestParam int page,
             @RequestParam int size){
        ApiResponse<List<CategoryViewResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get categories successfully",
                categoryService.viewListCategoryByClinic(clinicId, page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryViewResponse>> createNewCategory(@Valid @RequestBody CategoryCreateRequest request){
        CategoryViewResponse newCategory = categoryService.createNewCategory(authenticationService.getUserInfo(), request);
        ApiResponse<CategoryViewResponse> response = new ApiResponse<>(
                HttpStatus.CREATED,
                "Created category successfully",
                newCategory);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CategoryViewResponse>> updateCategory(@Valid @RequestBody CategoryUpdateRequest request){
        CategoryViewResponse updateCategory = categoryService.updateCategory(authenticationService.getUserInfo(), request);
        ApiResponse<CategoryViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Updated category successfully",
                updateCategory);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryViewResponse>> deleteCategory(@PathVariable("categoryId") Long categoryId){
        CategoryViewResponse deleteCategory = categoryService.deleteCategory(authenticationService.getUserInfo(), categoryId);
        ApiResponse<CategoryViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Updated category successfully",
                deleteCategory);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
