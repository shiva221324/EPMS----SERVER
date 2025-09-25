package com.epms.payroll.Repositories;

import com.epms.payroll.Entities.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

    boolean existsByDesignationCode(String designationCode);
}