package com.example.employeeprofile.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*") // Enable CORS for frontend
public class EmpController {

    @Autowired
    private EmployeeService employeeService;

    // Get all employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    // Get employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get employee by employeeId (for profile view)
    @GetMapping("/employeeId/{employeeId}")
    public ResponseEntity<Employee> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        Optional<Employee> employee = employeeService.getEmployeeByEmployeeId(employeeId);
        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new employee
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        Employee savedEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(savedEmployee);
    }

    // Update entire employee
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(updatedEmployee);
    }

    // Update specific section dynamically (e.g., for edit modal)
    @PatchMapping("/employeeId/{employeeId}/sections/{sectionName}")
    public ResponseEntity<Employee> updateSection(
            @PathVariable String employeeId,
            @PathVariable String sectionName,
            @RequestBody Object sectionData) {
        Employee updatedEmployee = employeeService.updateSection(employeeId, sectionName, sectionData);
        return ResponseEntity.ok(updatedEmployee);
    }

    // Add/update work experience entry
    @PostMapping("/employeeId/{employeeId}/work-experience")
    public ResponseEntity<Employee> addWorkExperience(
            @PathVariable String employeeId,
            @RequestBody WorkExperience workExperience) {
        Optional<Employee> optionalEmployee = employeeService.getEmployeeByEmployeeId(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            workExperience.setEmployee(employee);
            employee.getPreviousWorkExperience().add(workExperience);
            Employee updated = employeeService.updateEmployee(employee.getId(), employee);
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete work experience entry
    @DeleteMapping("/employeeId/{employeeId}/work-experience/{experienceId}")
    public ResponseEntity<Employee> deleteWorkExperience(
            @PathVariable String employeeId,
            @PathVariable Long experienceId) {
        // Implementation: Find and remove specific WorkExperience
        // For brevity, assume service method handles it
        return ResponseEntity.ok().build(); // Placeholder
    }

    // Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // Search employees
    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String employeeId) {
        List<Employee> employees = employeeService.searchEmployees(name, employeeId);
        return ResponseEntity.ok(employees);
    }
}