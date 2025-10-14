package com.epms.payroll.Services;


import com.epms.payroll.Entities.Employee;
import com.epms.payroll.Entities.Role;
import com.epms.payroll.Entities.User;
import com.epms.payroll.Repositories.EmployeeRepository;
import com.epms.payroll.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        System.out.println(employeeRepository.findById(7L));
        return employeeRepository.findById(id);
    }

    public Optional<Employee> getEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId);
    }

    public Employee createEmployee(Employee employee) {
        if (employee.getUser() == null) {
            User user = new User();
            user.setUsername(employee.getEmployeeId());
            user.setPassword(passwordEncoder.encode("1234"));

            // ✅ Fetch role from DB
            Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                    .orElseThrow(() -> new RuntimeException("Role EMPLOYEE not found"));

            user.setRoles(Set.of(employeeRole));

            employee.setUser(user);
        }

        if (employee.getPreviousWorkExperience() != null) {
            employee.getPreviousWorkExperience().forEach(we -> we.setEmployee(employee));
        }

        return employeeRepository.save(employee);
    }



    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    // Update basic fields
                    employee.setName(updatedEmployee.getName());
                    employee.setAvatarUrl(updatedEmployee.getAvatarUrl());
                    employee.setHeaderInfo(updatedEmployee.getHeaderInfo());
                    employee.setProfessionalSummary(updatedEmployee.getProfessionalSummary());

                    // Update dynamic sections using helper method
                    if (updatedEmployee.getPersonalDetails() != null) {
                        employee.setPersonalDetails(updatedEmployee.getPersonalDetails());
                    }
                    if (updatedEmployee.getContactDetails() != null) {
                        employee.setContactDetails(updatedEmployee.getContactDetails());
                    }
                    if (updatedEmployee.getEmergencyContact() != null) {
                        employee.setEmergencyContact(updatedEmployee.getEmergencyContact());
                    }
                    // ... similarly for other sections

                    // Handle work experience updates
                    if (updatedEmployee.getPreviousWorkExperience() != null) {
                        // Clear existing and add new
                        employee.getPreviousWorkExperience().clear();
                        updatedEmployee.getPreviousWorkExperience().forEach(we -> {
                            we.setEmployee(employee);
                            employee.getPreviousWorkExperience().add(we);
                        });
                    }

                    // Update other dynamic sections
                    if (updatedEmployee.getVisaDetails() != null) employee.setVisaDetails(updatedEmployee.getVisaDetails());
                    if (updatedEmployee.getGroupHealthInsurance() != null) employee.setGroupHealthInsurance(updatedEmployee.getGroupHealthInsurance());
                    // ... add for all other JSONB fields

                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    // Dynamic section update method
    public Employee updateSection(Long employeeId, String sectionName, Object sectionData) {
        return getEmployeeByEmployeeId(String.valueOf(employeeId))
                .map(employee -> {
                    employee.updateSection(sectionName, sectionData);
                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new RuntimeException("Employee not found with employeeId: " + employeeId));
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> searchEmployees(String name, String employeeId) {
        return employeeRepository.searchEmployees(name, employeeId);
    }
}