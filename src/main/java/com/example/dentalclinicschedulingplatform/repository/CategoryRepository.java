package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findById(Long categoryId);
    Category findByCategoryNameAndClinicId(String categoryName, Long clinicId);
    List<Category> findCategoriesByClinicId(Long clinicId);
}
