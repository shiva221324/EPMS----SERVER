package com.epms.payroll.Repositories;

import com.epms.payroll.Entities.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
    // Custom query to find logs by employee ID
    List<AttendanceLog> findByEmployee_EmployeeId(Long employeeId);
}