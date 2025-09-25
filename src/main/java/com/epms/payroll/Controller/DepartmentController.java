package com.epms.payroll.Controller;

import com.epms.payroll.Dto.DepartmentDto;
import com.epms.payroll.Entities.Department;
import com.epms.payroll.Exception.codes.ErrorCode;
import com.epms.payroll.Exception.custom.BusinessException;
import com.epms.payroll.Exception.custom.ResourceNotFoundException;
import com.epms.payroll.Repositories.DepartmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto) {

        // ✅ Check for duplicate department code
        if (departmentRepository.existsByDepartmentCode(departmentDto.getDepartmentCode())) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.CONFLICT,
                    "Department code already exists: " + departmentDto.getDepartmentCode()
            );
        }

        Department department = mapToEntity(departmentDto);
        Department savedDepartment = departmentRepository.save(department);
        return new ResponseEntity<>(mapToDto(savedDepartment), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return ResponseEntity.ok(mapToDto(department));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departmentDtos = departmentRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(departmentDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable Long id,
                                                          @RequestBody DepartmentDto departmentDto) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));

        existingDepartment.setDepartmentName(departmentDto.getDepartmentName());
        existingDepartment.setDepartmentCode(departmentDto.getDepartmentCode());
        existingDepartment.setDescription(departmentDto.getDescription());

        if (departmentDto.getParentDepartmentId() != null) {
            Department parent = departmentRepository.findById(departmentDto.getParentDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent Department not found with id: " + departmentDto.getParentDepartmentId()
                    ));
            existingDepartment.setParentDepartment(parent);
        } else {
            existingDepartment.setParentDepartment(null);
        }

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return ResponseEntity.ok(mapToDto(updatedDepartment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helper Methods ---

    private DepartmentDto mapToDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setDepartmentId(department.getDepartmentId());
        dto.setDepartmentName(department.getDepartmentName());
        dto.setDepartmentCode(department.getDepartmentCode());
        dto.setDescription(department.getDescription());
        if (department.getParentDepartment() != null) {
            dto.setParentDepartmentId(department.getParentDepartment().getDepartmentId());
        }
        return dto;
    }

    private Department mapToEntity(DepartmentDto dto) {
        Department department = new Department();
        department.setDepartmentName(dto.getDepartmentName());
        department.setDepartmentCode(dto.getDepartmentCode());
        department.setDescription(dto.getDescription());

        if (dto.getParentDepartmentId() != null) {
            Department parent = departmentRepository.findById(dto.getParentDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent Department not found with id: " + dto.getParentDepartmentId()
                    ));
            department.setParentDepartment(parent);
        }
        return department;
    }
}
