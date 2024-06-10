package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>{

    Optional<Category> findById(Long categoryId);
    Category findByCategoryNameAndClinic_ClinicId(String categoryName, Long clinicId);
    List<Category> findCategoriesByClinic_ClinicId(Long clinicId);
//    @Query(value = "SELECT * FROM Category " +
//            "WHERE clinic_id = :clinicId " +
//            "OFFSET :offSet ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    Page<Category> findCategoriesByClinic_ClinicId(Long clinicId, Pageable pageable);
    Page<Category> findAll(Pageable pageable);
}
