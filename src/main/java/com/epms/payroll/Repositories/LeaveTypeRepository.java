package com.epms.payroll.Repositories;

import com.epms.payroll.Entities.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    Optional<Object> findByLeaveCode(String leaveCode);
}