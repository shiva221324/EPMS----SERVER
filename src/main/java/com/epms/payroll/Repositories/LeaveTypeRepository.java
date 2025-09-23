package com.epms.payroll.Repositories;

import com.epms.payroll.Entities.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
}