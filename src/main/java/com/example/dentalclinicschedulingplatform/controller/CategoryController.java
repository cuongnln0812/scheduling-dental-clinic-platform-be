package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.CategoryCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CategoryUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.PaginationRequest;
import com.example.dentalclinicschedulingplatform.payload.response.ApiResponse;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
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

    @GetMapping("/view-category-by-clinic")
    public ResponseEntity<ApiResponse<List<CategoryViewResponse>>> viewCategoryListByClinic(@RequestParam Long clinicId, @Valid PaginationRequest paginationRequest){
        ApiResponse<List<CategoryViewResponse>> response = new ApiResponse<>(
                HttpStatus.OK,
                "Get categories successfully",
                categoryService.viewListCategoryByClinic(clinicId, paginationRequest));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CategoryViewResponse>> createNewCategory(@Valid @RequestBody CategoryCreateRequest request){
        CategoryViewResponse newCategory = categoryService.createNewCategory(authenticationService.getUserInfo(), request);
        ApiResponse<CategoryViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Created category successfully",
                newCategory);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<CategoryViewResponse>> updateCategory(@Valid @RequestBody CategoryUpdateRequest request){
        CategoryViewResponse updateCategory = categoryService.updateCategory(authenticationService.getUserInfo(), request);
        ApiResponse<CategoryViewResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Updated category successfully",
                updateCategory);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
