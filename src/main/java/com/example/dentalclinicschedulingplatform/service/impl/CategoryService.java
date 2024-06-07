package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.CategoryCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CategoryUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.CategoryViewResponse;
import com.example.dentalclinicschedulingplatform.payload.response.ServiceViewResponse;
import com.example.dentalclinicschedulingplatform.payload.response.UserInformationRes;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.ICategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ClinicRepository clinicRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private DentalService dentalService;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ClinicBranchRepository clinicBranchRepository;
    @Override
    public List<CategoryViewResponse> viewListCategoryByClinic(Long clinicId) {

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        List<Category> categories = categoryRepository.findCategoriesByClinicId(clinicId);
        List<CategoryViewResponse> categoryViewResponseList = new ArrayList<>();
        for (Category categoryItem : categories) {
            if (categoryItem.isStatus()) {
                List<ServiceViewResponse> serviceViewResponseList = dentalService.viewServicesByCategoryId(categoryItem.getId());
                categoryViewResponseList.add(new CategoryViewResponse(categoryItem.getId(),
                        categoryItem.getCategoryName(), categoryItem.isStatus(), serviceViewResponseList));
            }
        }
        return categoryViewResponseList;
    }

    @Override
    public CategoryViewResponse createNewCategory(UserInformationRes userInformation, CategoryCreateRequest request) {
        if (!userInformation.getRole().equals(UserType.OWNER.toString())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }
        ClinicOwner owner = ownerRepository.findByUsernameOrEmail(userInformation.getUsername(), userInformation.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner does not exist"));

        Clinic clinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

        Category category = categoryRepository.findByCategoryNameAndClinicId(request.getCategoryName(), clinic.getId());

        if (category != null){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Category name is already existed");
        }

        Category newCategory = new Category();
        newCategory.setCategoryName(request.getCategoryName());
        newCategory.setCreatedBy(userInformation.getEmail());
        newCategory.setCreatedDate(LocalDateTime.now());
        newCategory.setStatus(true);
        newCategory.setClinic(clinic);

        categoryRepository.save(newCategory);

        return new CategoryViewResponse(newCategory.getId(), newCategory.getCategoryName(), newCategory.isStatus(), null);
    }

    @Override
    public CategoryViewResponse updateCategory(UserInformationRes userInformation, CategoryUpdateRequest request) {
        if (!userInformation.getRole().equals(UserType.OWNER.toString()) && !userInformation.getRole().equals(UserType.STAFF.toString())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

        ClinicOwner owner = ownerRepository.findByUsernameOrEmail(userInformation.getUsername(), userInformation.getEmail())
                .orElse(null);

        if (owner == null) {
            ClinicStaff staff = staffRepository.findByUsernameOrEmail(userInformation.getUsername(), userInformation.getEmail())
                    .orElse(null);
            if (staff == null) {
                throw new ApiException(HttpStatus.NOT_FOUND, "User does not exist");
            }
            Category updateCategory = categoryRepository.findById(request.categoryId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category does not exist"));

            ClinicBranch currBranch = clinicBranchRepository.findById(staff.getClinicBranch().getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Branch does not exist"));

            Clinic currClinic  = clinicRepository.findById(currBranch.getClinic().getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

            if (!updateCategory.getCategoryName().equals(request.getCategoryName())) {
                Category existingCategory = categoryRepository.findByCategoryNameAndClinicId(request.getCategoryName(), currClinic.getId());
                if (existingCategory != null){
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Category name is already existed");
                }
            }

            updateCategory.setCategoryName(request.getCategoryName());
            updateCategory.setModifiedBy(staff.getEmail());
            updateCategory.setModifiedDate(LocalDateTime.now());

            categoryRepository.save(updateCategory);
            return new CategoryViewResponse(updateCategory.getId(), updateCategory.getCategoryName(),
                    updateCategory.isStatus(), dentalService.viewServicesByCategoryId(updateCategory.getId()));
        }

        Category updateCategory = categoryRepository.findById(request.categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category does not exist"));

        Clinic currClinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

        if (!updateCategory.getCategoryName().equals(request.getCategoryName())) {
            Category existingCategory = categoryRepository.findByCategoryNameAndClinicId(request.getCategoryName(), currClinic.getId());
            if (existingCategory != null){
                throw new ApiException(HttpStatus.BAD_REQUEST, "Category name is already existed");
            }
        }

        updateCategory.setCategoryName(request.getCategoryName());
        updateCategory.setModifiedBy(owner.getEmail());
        updateCategory.setModifiedDate(LocalDateTime.now());

        categoryRepository.save(updateCategory);
        return new CategoryViewResponse(updateCategory.getId(), updateCategory.getCategoryName(),
                updateCategory.isStatus(), dentalService.viewServicesByCategoryId(updateCategory.getId()));
    }
}
