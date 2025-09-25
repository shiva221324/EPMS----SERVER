package com.epms.payroll.Repositories;

import com.epms.payroll.Entities.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    boolean existsByEmployee_EmployeeIdAndLeaveType_LeaveTypeIdAndYear(Long employeeId, Long leaveTypeId, Integer year);
}