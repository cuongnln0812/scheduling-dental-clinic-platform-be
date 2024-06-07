package com.example.dentalclinicschedulingplatform.controller;

import com.example.dentalclinicschedulingplatform.payload.request.CategoryCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CategoryUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.service.IAuthenticateService;
import com.example.dentalclinicschedulingplatform.service.impl.AuthenticateService;
import com.example.dentalclinicschedulingplatform.service.impl.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<CategoryViewResponse>> viewCategoryListByClinic(@RequestParam Long clinicId){
        List<CategoryViewResponse> categories = categoryService.viewListCategoryByClinic(clinicId);
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/create")
    public ResponseEntity<CategoryViewResponse> createNewCategory(@Valid @RequestBody CategoryCreateRequest request){
        CategoryViewResponse newCategory = categoryService.createNewCategory(authenticationService.getUserInfo(), request);
        return ResponseEntity.ok(newCategory);
    }

    @PostMapping("/update")
    public ResponseEntity<CategoryViewResponse> updateCategory(@Valid @RequestBody CategoryUpdateRequest request){
        CategoryViewResponse updateCategory = categoryService.updateCategory(authenticationService.getUserInfo(), request);
        return ResponseEntity.ok(updateCategory);
    }
}
