package com.epms.payroll.Controller;

import com.epms.payroll.Dto.DepartmentDto;
import com.epms.payroll.Entities.Department;
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
        Department department = mapToEntity(departmentDto);
        Department savedDepartment = departmentRepository.save(department);
        return new ResponseEntity<>(mapToDto(savedDepartment), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        return departmentRepository.findById(id)
                .map(department -> ResponseEntity.ok(mapToDto(department)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        List<DepartmentDto> departmentDtos = departments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(departmentDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDto departmentDto) {
        return departmentRepository.findById(id)
                .map(existingDepartment -> {
                    existingDepartment.setDepartmentName(departmentDto.getDepartmentName());
                    existingDepartment.setDepartmentCode(departmentDto.getDepartmentCode());
                    existingDepartment.setDescription(departmentDto.getDescription());

                    if (departmentDto.getParentDepartmentId() != null) {
                        Department parent = departmentRepository.findById(departmentDto.getParentDepartmentId())
                                .orElseThrow(() -> new RuntimeException("Parent Department not found"));
                        existingDepartment.setParentDepartment(parent);
                    } else {
                        existingDepartment.setParentDepartment(null);
                    }

                    Department updatedDepartment = departmentRepository.save(existingDepartment);
                    return ResponseEntity.ok(mapToDto(updatedDepartment));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        if (!departmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        departmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helper Methods for Mapping ---

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
                    .orElseThrow(() -> new RuntimeException("Parent Department not found with id: " + dto.getParentDepartmentId()));
            department.setParentDepartment(parent);
        }
        return department;
    }
}