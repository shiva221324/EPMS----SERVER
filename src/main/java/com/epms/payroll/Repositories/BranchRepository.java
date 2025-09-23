package com.epms.payroll.Repositories;

import com.epms.payroll.Entities.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}