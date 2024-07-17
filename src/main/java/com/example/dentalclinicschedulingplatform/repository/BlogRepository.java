package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Blog;
import com.example.dentalclinicschedulingplatform.entity.Clinic;
import com.example.dentalclinicschedulingplatform.entity.ClinicStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    Page<Blog> findAllByStatus(ClinicStatus status, Pageable pageable);
    Page<Blog> findAllByStatusOrStatus(ClinicStatus status, ClinicStatus status2, Pageable pageable);
    Page<Blog> findAllByClinic_ClinicId(Long id, Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchValue, '%'))")

    Page<Blog> findBlogs (String searchValue, Pageable pageable);
}
