package com.example.dentalclinicschedulingplatform.repository;

import com.example.dentalclinicschedulingplatform.entity.Category;
import com.example.dentalclinicschedulingplatform.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    @Override
    Optional<Service> findById(Long serviceId);
    List<Service> findServicesByCategoryId(Long categoryId);
    List<Service> findServicesByClinic_ClinicId(Long clinicId);
    Page<Service> findServicesByClinic_ClinicId(Long clinicId, Pageable pageable);
    Page<Service> findAll(Pageable pageable);
    Service findByServiceNameAndClinic_ClinicId(String serviceName, Long clinicId);
}
