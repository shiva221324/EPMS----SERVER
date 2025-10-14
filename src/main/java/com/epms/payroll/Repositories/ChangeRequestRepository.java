package com.epms.payroll.Repositories;

import com.epms.payroll.Entities.ChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long> {
    List<ChangeRequest> findByStatus(ChangeRequest.ChangeRequestStatus status);
    List<ChangeRequest> findByEmployeeEmployeeIdAndStatus(String employeeEmployeeId, ChangeRequest.ChangeRequestStatus status);
}