package com.epms.payroll.Repositories;

import com.epms.payroll.Entities.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
}