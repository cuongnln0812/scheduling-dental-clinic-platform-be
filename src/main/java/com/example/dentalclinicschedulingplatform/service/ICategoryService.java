package com.example.dentalclinicschedulingplatform.service;

import com.example.dentalclinicschedulingplatform.payload.request.CategoryCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CategoryUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;

import java.util.List;

public interface ICategoryService {
    List<CategoryViewResponse> viewListCategoryByClinic(Long clinicId);
    CategoryViewResponse createNewCategory(UserInformationRes userInformation, CategoryCreateRequest request);
    CategoryViewResponse updateCategory(UserInformationRes userInformation, CategoryUpdateRequest request);
}
