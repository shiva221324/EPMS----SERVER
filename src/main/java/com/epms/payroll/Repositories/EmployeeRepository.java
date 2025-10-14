package com.epms.payroll.Repositories;

import com.epms.payroll.Entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeId(String employeeId);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :employeeId, '%'))")
    List<Employee> searchEmployees(@Param("name") String name, @Param("employeeId") String employeeId);
}