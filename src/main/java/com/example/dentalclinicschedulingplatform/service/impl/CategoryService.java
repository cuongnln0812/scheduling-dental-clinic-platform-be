package com.example.dentalclinicschedulingplatform.service.impl;

import com.example.dentalclinicschedulingplatform.entity.*;
import com.example.dentalclinicschedulingplatform.exception.ApiException;
import com.example.dentalclinicschedulingplatform.payload.request.CategoryCreateRequest;
import com.example.dentalclinicschedulingplatform.payload.request.CategoryUpdateRequest;
import com.example.dentalclinicschedulingplatform.payload.response.*;
import com.example.dentalclinicschedulingplatform.repository.*;
import com.example.dentalclinicschedulingplatform.service.ICategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ClinicRepository clinicRepository;
    private final OwnerRepository ownerRepository;
    private final DentalService dentalService;
    private final StaffRepository staffRepository;
    private final ClinicBranchRepository clinicBranchRepository;
    private final ModelMapper modelMapper;
    @Override
    public Map<String, Object> viewListCategoryByClinic(Long clinicId, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);
        List<CategoryViewResponse> categoriesByClinic = new ArrayList<>();
        List<CategoryViewListResponse> allCategories = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();

        if (clinicId == null) {
            Page<Category> categories = categoryRepository.findAll(pageRequest);
            for (Category categoryItem : categories) {
                allCategories.add(modelMapper.map(categoryItem, CategoryViewListResponse.class));
            }
            allCategories.sort(Comparator.comparing(CategoryViewListResponse::getId));

            result.put("Categories", allCategories);
            return result;

        }else {
            Clinic clinic = clinicRepository.findById(clinicId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

            Page<Category> categories = categoryRepository.findCategoriesByClinic_ClinicId(clinic.getClinicId(), pageRequest);
            for (Category categoryItem : categories) {
                List<ServiceViewDetailsResponse> serviceViewDetailsResponseList = dentalService.viewServicesByCategoryId(categoryItem.getId());
                categoriesByClinic.add(new CategoryViewResponse(categoryItem.getId(), categoryItem.getCategoryName(), categoryItem.getCategoryImage(),
                        categoryItem.isStatus(), serviceViewDetailsResponseList));
            }
            categoriesByClinic.sort(Comparator.comparing(CategoryViewResponse::getId));

            result.put("Categories by clinic", categoriesByClinic);
            return result;
        }
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

        Category category = categoryRepository.findByCategoryNameAndClinic_ClinicId(request.getCategoryName(), clinic.getClinicId());

        if (category != null){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Category name is already existed");
        }

        Category newCategory = new Category();
        newCategory.setCategoryName(request.getCategoryName());
        newCategory.setCreatedBy(userInformation.getUsername());
        newCategory.setCreatedDate(LocalDateTime.now());
        newCategory.setStatus(true);
        newCategory.setCategoryImage(request.getCategoryImage());
        newCategory.setClinic(clinic);

        categoryRepository.save(newCategory);

        return modelMapper.map(newCategory, CategoryViewResponse.class);
    }

    @Override
    public CategoryViewResponse updateCategory(UserInformationRes userInformation, CategoryUpdateRequest request) {
        if (!userInformation.getRole().equals(UserType.OWNER.toString()) && !userInformation.getRole().equals(UserType.STAFF.toString())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permission");
        }

        Category updateCategory = categoryRepository.findById(request.categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category does not exist"));

        ClinicOwner owner = ownerRepository.findByUsernameOrEmail(userInformation.getUsername(), userInformation.getEmail())
                .orElse(null);

        if (owner == null) {
            ClinicStaff staff = staffRepository.findByUsernameOrEmail(userInformation.getUsername(), userInformation.getEmail())
                    .orElse(null);
            if (staff == null) {
                throw new ApiException(HttpStatus.NOT_FOUND, "User does not exist");
            }

            long clinicId = staffRepository.getClinicIdOfStaff(staff.getId());

            Clinic currClinic  = clinicRepository.findById(clinicId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

            List<Category> categories = categoryRepository.findCategoriesByClinic_ClinicId(currClinic.getClinicId());

            if (!categories.contains(updateCategory)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Category does not belong to current clinic");
            }

            if (!updateCategory.getCategoryName().equalsIgnoreCase(request.getCategoryName())) {
                Category existingCategory = categoryRepository.findByCategoryNameAndClinic_ClinicId(request.getCategoryName(), currClinic.getClinicId());
                if (existingCategory != null){
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Category name is already existed");
                }
            }

            updateCategory.setCategoryName(request.getCategoryName());
            updateCategory.setModifiedBy(staff.getUsername());
            updateCategory.setModifiedDate(LocalDateTime.now());
            updateCategory.setStatus(request.categoryStatus);
            updateCategory.setCategoryImage(request.getCategoryImage());

            categoryRepository.save(updateCategory);
            return new CategoryViewResponse(updateCategory.getId(), updateCategory.getCategoryName(), updateCategory.getCategoryImage(),
                    updateCategory.isStatus(), dentalService.viewServicesByCategoryId(updateCategory.getId()));
        }
        Clinic currClinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

        List<Category> categories = categoryRepository.findCategoriesByClinic_ClinicId(currClinic.getClinicId());

        if (!categories.contains(updateCategory)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Category does not belong to current clinic");
        }

        if (!updateCategory.getCategoryName().equalsIgnoreCase(request.getCategoryName())) {
            Category existingCategory = categoryRepository.findByCategoryNameAndClinic_ClinicId(request.getCategoryName(), currClinic.getClinicId());
            if (existingCategory != null){
                throw new ApiException(HttpStatus.BAD_REQUEST, "Category name is already existed");
            }
        }

        updateCategory.setCategoryName(request.getCategoryName());
        updateCategory.setModifiedBy(owner.getUsername());
        updateCategory.setModifiedDate(LocalDateTime.now());
        updateCategory.setStatus(request.categoryStatus);
        updateCategory.setCategoryImage(request.getCategoryImage());

        categoryRepository.save(updateCategory);
        return new CategoryViewResponse(updateCategory.getId(), updateCategory.getCategoryName(), updateCategory.getCategoryImage(),
                updateCategory.isStatus(), dentalService.viewServicesByCategoryId(updateCategory.getId()));
    }

    @Override
    public CategoryViewResponse deleteCategory(UserInformationRes userInformation, Long categoryId) {
        if (!userInformation.getRole().equals(UserType.OWNER.toString())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Do not have permission");
        }

        Category deletedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category does not exist"));

        ClinicOwner owner = ownerRepository.findByUsernameOrEmail(userInformation.getUsername(), userInformation.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner does not exist"));

        Clinic currClinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic does not exist"));

        List<Category> categories = categoryRepository.findCategoriesByClinic_ClinicId(currClinic.getClinicId());

        if (!categories.contains(deletedCategory)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Category does not belong to current clinic");
        }

        if (deletedCategory.isStatus()) {
            deletedCategory.setStatus(false);
        }else {
            throw new ApiException(HttpStatus.CONFLICT, "The category is already been deactivated");
        }

        deletedCategory.setModifiedDate(LocalDateTime.now());
        deletedCategory.setModifiedBy(owner.getUsername());

        categoryRepository.save(deletedCategory);

        return new CategoryViewResponse(deletedCategory.getId(), deletedCategory.getCategoryName(), currClinic.getClinicImage(),
                deletedCategory.isStatus(), dentalService.viewServicesByCategoryId(deletedCategory.getId()));
    }

    @Override
    public CategoryViewResponse changeCategoryStatus(UserInformationRes userInformation, Long categoryId, boolean status) {

        if (!userInformation.getRole().equals(UserType.OWNER.toString())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Do not have permission");
        }

        Category currCategory  = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

        ClinicOwner owner = ownerRepository.findByUsername(userInformation.getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner not found"));

        Clinic clinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        List<Category> categories = categoryRepository.findCategoriesByClinic_ClinicId(clinic.getClinicId());

        if (!categories.contains(currCategory)){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Category does not belong to current clinic");
        }

        if (currCategory.isStatus() && status) {
            throw new ApiException(HttpStatus.CONFLICT, "The category status is already ACTIVE");
        }else if (!currCategory.isStatus() && !status){
            throw new ApiException(HttpStatus.CONFLICT, "The category status is already INACTIVE");
        }

        currCategory.setStatus(status);
        currCategory.setModifiedBy(owner.getUsername());
        currCategory.setModifiedDate(LocalDateTime.now());
        categoryRepository.save(currCategory);

        return new CategoryViewResponse(currCategory.getId(), currCategory.getCategoryName(), currCategory.getCategoryImage(),
                currCategory.isStatus(), dentalService.viewServicesByCategoryId(currCategory.getId()));
    }

    @Override
    public CategoryViewResponse viewDetailsCategory(UserInformationRes userInformation, long categoryId) {
        if (!userInformation.getRole().equals(UserType.OWNER.toString())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Do not have permisson");
        }

        ClinicOwner owner = ownerRepository.findByUsername(userInformation.getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Owner not found"));

        Clinic clinic = clinicRepository.findByClinicOwnerId(owner.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clinic not found"));

        List<Category> categories = categoryRepository.findCategoriesByClinic_ClinicId(clinic.getClinicId());

        Category currCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

        if (!categories.contains(currCategory)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current category does not belongs to the clinic of owner");
        }

        return modelMapper.map(currCategory, CategoryViewResponse.class);
    }
}
