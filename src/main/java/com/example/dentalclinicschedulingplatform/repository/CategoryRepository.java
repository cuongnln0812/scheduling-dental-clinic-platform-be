package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Category;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findById(Long categoryId);
    Category findByCategoryNameAndClinicId(String categoryName, Long clinicId);
    List<Category> findCategoriesByClinicId(Long clinicId);
    @Query(value = "SELECT * FROM Category " +
            "WHERE clinic_id = :clinicId " +
            "OFFSET :offSet ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Category> findCategoriesByClinicId(Long clinicId, int offSet, int limit);
}
