package com.epms.payroll.Controller;

import com.epms.payroll.Dto.EmployeeDto;
import com.epms.payroll.Entities.*;
import com.epms.payroll.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    // Constructor-based dependency injection
    public EmployeeController(EmployeeRepository employeeRepository,
                              DepartmentRepository departmentRepository,
                              DesignationRepository designationRepository,
                              BranchRepository branchRepository,
                              RoleRepository roleRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.designationRepository = designationRepository;
        this.branchRepository = branchRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
        // 1️⃣ Map DTO to Employee entity
        System.out.println("create employee");
        Employee employee = mapToEntity(employeeDto);

        // 2️⃣ Create User object for login
        User user = new User();
        user.setUsername(employeeDto.getUsername()); // get from DTO
        user.setEmail(employeeDto.getEmail());       // same as employee email
        user.setPassword(passwordEncoder.encode(employeeDto.getPassword())); // encode password


        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("ROLE_EMPLOYEE not found"));
        user.setRoles(Set.of(employeeRole));




        employee.setUser(user);

        // 4️⃣ Save Employee (User will also be saved due to CascadeType.ALL)
        Employee savedEmployee = employeeRepository.save(employee);

        // 5️⃣ Return DTO
        return new ResponseEntity<>(mapToDto(savedEmployee), HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(employee -> ResponseEntity.ok(mapToDto(employee)))
                .orElse(ResponseEntity.notFound().build());
    }



    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDto> employeeDtos = employees.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employeeDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) {
        return employeeRepository.findById(id)
                .map(existingEmployee -> {
                    // Update all fields
                    existingEmployee.setEmployeeCode(employeeDto.getEmployeeCode());
                    existingEmployee.setFirstName(employeeDto.getFirstName());
                    existingEmployee.setLastName(employeeDto.getLastName());
                    existingEmployee.setMiddleName(employeeDto.getMiddleName());
                    existingEmployee.setEmail(employeeDto.getEmail());
                    existingEmployee.setPhone(employeeDto.getPhone());
                    existingEmployee.setDateOfBirth(employeeDto.getDateOfBirth());
                    existingEmployee.setGender(employeeDto.getGender());
                    existingEmployee.setMaritalStatus(employeeDto.getMaritalStatus());
                    existingEmployee.setBloodGroup(employeeDto.getBloodGroup());
                    existingEmployee.setPanNumber(employeeDto.getPanNumber());
                    existingEmployee.setAadhaarNumber(employeeDto.getAadhaarNumber());
                    existingEmployee.setPassportNumber(employeeDto.getPassportNumber());
                    existingEmployee.setDrivingLicense(employeeDto.getDrivingLicense());
                    existingEmployee.setAddressPermanent(employeeDto.getAddressPermanent());
                    existingEmployee.setAddressCurrent(employeeDto.getAddressCurrent());
                    existingEmployee.setEmergencyContactName(employeeDto.getEmergencyContactName());
                    existingEmployee.setEmergencyContactPhone(employeeDto.getEmergencyContactPhone());
                    existingEmployee.setEmergencyContactRelation(employeeDto.getEmergencyContactRelation());
                    existingEmployee.setPhotoPath(employeeDto.getPhotoPath());
                    existingEmployee.setStatus(employeeDto.getStatus());

                    // Update relationships
                    if (employeeDto.getDepartmentId() != null) {
                        Department department = departmentRepository.findById(employeeDto.getDepartmentId())
                                .orElseThrow(() -> new RuntimeException("Department not found with id: " + employeeDto.getDepartmentId()));
                        existingEmployee.setDepartment(department);
                    }
                    if (employeeDto.getDesignationId() != null) {
                        Designation designation = designationRepository.findById(employeeDto.getDesignationId())
                                .orElseThrow(() -> new RuntimeException("Designation not found with id: " + employeeDto.getDesignationId()));
                        existingEmployee.setDesignation(designation);
                    }
                    if (employeeDto.getBranchId() != null) {
                        Branch branch = branchRepository.findById(employeeDto.getBranchId())
                                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + employeeDto.getBranchId()));
                        existingEmployee.setBranch(branch);
                    }
                    if (employeeDto.getCreatedById() != null) {
                        Employee createdBy = employeeRepository.findById(employeeDto.getCreatedById())
                                .orElseThrow(() -> new RuntimeException("CreatedBy Employee not found with id: " + employeeDto.getCreatedById()));
                        existingEmployee.setCreatedBy(createdBy);
                    }
                    if (employeeDto.getUpdatedById() != null) {
                        Employee updatedBy = employeeRepository.findById(employeeDto.getUpdatedById())
                                .orElseThrow(() -> new RuntimeException("UpdatedBy Employee not found with id: " + employeeDto.getUpdatedById()));
                        existingEmployee.setUpdatedBy(updatedBy);
                    }

                    // Note: Not updating User or audit fields (createdAt, updatedAt) as they are managed by the system
                    Employee updatedEmployee = employeeRepository.save(existingEmployee);
                    return ResponseEntity.ok(mapToDto(updatedEmployee));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        employeeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    // --- Helper Methods for DTO/Entity Mapping ---

    private EmployeeDto mapToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setEmployeeCode(employee.getEmployeeCode());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setMiddleName(employee.getMiddleName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setDateOfBirth(employee.getDateOfBirth());
        dto.setGender(employee.getGender());
        dto.setMaritalStatus(employee.getMaritalStatus());
        dto.setBloodGroup(employee.getBloodGroup());
        dto.setPanNumber(employee.getPanNumber());
        dto.setAadhaarNumber(employee.getAadhaarNumber());
        dto.setPassportNumber(employee.getPassportNumber());
        dto.setDrivingLicense(employee.getDrivingLicense());
        dto.setAddressPermanent(employee.getAddressPermanent());
        dto.setAddressCurrent(employee.getAddressCurrent());
        dto.setEmergencyContactName(employee.getEmergencyContactName());
        dto.setEmergencyContactPhone(employee.getEmergencyContactPhone());
        dto.setEmergencyContactRelation(employee.getEmergencyContactRelation());
        dto.setPhotoPath(employee.getPhotoPath());
        dto.setStatus(employee.getStatus());

        // Relationships
        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getDepartmentId());
        }
        if (employee.getDesignation() != null) {
            dto.setDesignationId(employee.getDesignation().getDesignationId());
        }
        if (employee.getBranch() != null) {
            dto.setBranchId(employee.getBranch().getBranchId());
        }
        if (employee.getUser() != null) {
            dto.setUserId(employee.getUser().getId());
            dto.setUsername(employee.getUser().getUsername());
        }
        if (employee.getCreatedBy() != null) {
            dto.setCreatedById(employee.getCreatedBy().getEmployeeId());
        }
        if (employee.getUpdatedBy() != null) {
            dto.setUpdatedById(employee.getUpdatedBy().getEmployeeId());
        }

        // Audit Fields
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());

        return dto;
    }

    private Employee mapToEntity(EmployeeDto dto) {
        Employee employee = new Employee();
        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setMiddleName(dto.getMiddleName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setGender(dto.getGender());
        employee.setMaritalStatus(dto.getMaritalStatus());
        employee.setBloodGroup(dto.getBloodGroup());
        employee.setPanNumber(dto.getPanNumber());
        employee.setAadhaarNumber(dto.getAadhaarNumber());
        employee.setPassportNumber(dto.getPassportNumber());
        employee.setDrivingLicense(dto.getDrivingLicense());
        employee.setAddressPermanent(dto.getAddressPermanent());
        employee.setAddressCurrent(dto.getAddressCurrent());
        employee.setEmergencyContactName(dto.getEmergencyContactName());
        employee.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        employee.setEmergencyContactRelation(dto.getEmergencyContactRelation());
        employee.setPhotoPath(dto.getPhotoPath());
        employee.setStatus(dto.getStatus());


        // Find and set related entities
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + dto.getDepartmentId()));
            employee.setDepartment(department);
        }
        if (dto.getDesignationId() != null) {
            Designation designation = designationRepository.findById(dto.getDesignationId())
                    .orElseThrow(() -> new RuntimeException("Designation not found with id: " + dto.getDesignationId()));
            employee.setDesignation(designation);
        }
        if (dto.getBranchId() != null) {
            Branch branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found with id: " + dto.getBranchId()));
            employee.setBranch(branch);
        }
        if (dto.getCreatedById() != null) {
            Employee createdBy = employeeRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("CreatedBy Employee not found with id: " + dto.getCreatedById()));
            employee.setCreatedBy(createdBy);
        }
        if (dto.getUpdatedById() != null) {
            Employee updatedBy = employeeRepository.findById(dto.getUpdatedById())
                    .orElseThrow(() -> new RuntimeException("UpdatedBy Employee not found with id: " + dto.getUpdatedById()));
            employee.setUpdatedBy(updatedBy);
        }

        return employee;
    }
}