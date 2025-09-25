package com.epms.payroll.Controller;

import com.epms.payroll.Dto.LeaveTypeDto;
import com.epms.payroll.Entities.LeaveType;
import com.epms.payroll.Exception.codes.ErrorCode;
import com.epms.payroll.Exception.custom.BusinessException;
import com.epms.payroll.Exception.custom.ResourceNotFoundException;
import com.epms.payroll.Repositories.LeaveTypeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeController(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @PostMapping
    public ResponseEntity<LeaveTypeDto> createLeaveType(@Valid @RequestBody LeaveTypeDto dto) {
        validateLeaveTypeDto(dto);
        if (leaveTypeRepository.findByLeaveCode(dto.getLeaveCode()).isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.CONFLICT,
                    "Leave type with code " + dto.getLeaveCode() + " already exists"
            );
        }

        LeaveType leaveType = mapToEntity(dto);
        LeaveType saved = leaveTypeRepository.save(leaveType);
        return new ResponseEntity<>(mapToDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> getLeaveTypeById(@PathVariable Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + id));
        return ResponseEntity.ok(mapToDto(leaveType));
    }

    @GetMapping
    public ResponseEntity<List<LeaveTypeDto>> getAllLeaveTypes() {
        List<LeaveTypeDto> dtos = leaveTypeRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> updateLeaveType(@PathVariable Long id, @Valid @RequestBody LeaveTypeDto dto) {
        validateLeaveTypeDto(dto);
        LeaveType existing = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + id));

        // Check for duplicate leave code (excluding the current leave type)
        if (dto.getLeaveCode() != null && !dto.getLeaveCode().equals(existing.getLeaveCode())
                && leaveTypeRepository.findByLeaveCode(dto.getLeaveCode()).isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.CONFLICT,
                    "Leave type with code " + dto.getLeaveCode() + " already exists"
            );
        }

        updateLeaveTypeFields(existing, dto);
        LeaveType updated = leaveTypeRepository.save(existing);
        return ResponseEntity.ok(mapToDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        if (!leaveTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave type not found with id: " + id);
        }
        leaveTypeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helper Methods ---

    private void validateLeaveTypeDto(LeaveTypeDto dto) {
        if (dto.getLeaveName() == null || dto.getLeaveName().isBlank()) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Leave name is required and cannot be empty"
            );
        }
        if (dto.getLeaveCode() == null || dto.getLeaveCode().isBlank()) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Leave code is required and cannot be empty"
            );
        }
        if (dto.getLeaveCategory() == null || dto.getLeaveCategory().isBlank()) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Leave category is required and cannot be empty"
            );
        }
        if (dto.getMaxDaysPerYear() != null && dto.getMaxDaysPerYear().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Maximum days per year cannot be negative"
            );
        }

        if (dto.getMaxCarryForwardDays() != null && dto.getMaxCarryForwardDays().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Maximum carry forward days cannot be negative"
            );
        }

        // Add more validations as needed (e.g., valid status values, boolean checks)
    }

    private void updateLeaveTypeFields(LeaveType existing, LeaveTypeDto dto) {
        existing.setLeaveName(dto.getLeaveName());
        existing.setLeaveCode(dto.getLeaveCode());
        existing.setLeaveCategory(dto.getLeaveCategory());
        existing.setMaxDaysPerYear(dto.getMaxDaysPerYear());
        existing.setCarryForwardAllowed(dto.getCarryForwardAllowed());
        existing.setMaxCarryForwardDays(dto.getMaxCarryForwardDays());
        existing.setEncashmentAllowed(dto.getEncashmentAllowed());
        existing.setIsPaidLeave(dto.getIsPaidLeave());
        existing.setStatus(dto.getStatus());
    }

    private LeaveTypeDto mapToDto(LeaveType leaveType) {
        LeaveTypeDto dto = new LeaveTypeDto();
        dto.setLeaveTypeId(leaveType.getLeaveTypeId());
        dto.setLeaveName(leaveType.getLeaveName());
        dto.setLeaveCode(leaveType.getLeaveCode());
        dto.setLeaveCategory(leaveType.getLeaveCategory());
        dto.setMaxDaysPerYear(leaveType.getMaxDaysPerYear());
        dto.setCarryForwardAllowed(leaveType.getCarryForwardAllowed());
        dto.setMaxCarryForwardDays(leaveType.getMaxCarryForwardDays());
        dto.setEncashmentAllowed(leaveType.getEncashmentAllowed());
        dto.setIsPaidLeave(leaveType.getIsPaidLeave());
        dto.setStatus(leaveType.getStatus());
        dto.setCreatedAt(leaveType.getCreatedAt());
        dto.setUpdatedAt(leaveType.getUpdatedAt());
        return dto;
    }

    private LeaveType mapToEntity(LeaveTypeDto dto) {
        LeaveType leaveType = new LeaveType();
        leaveType.setLeaveName(dto.getLeaveName());
        leaveType.setLeaveCode(dto.getLeaveCode());
        leaveType.setLeaveCategory(dto.getLeaveCategory());
        leaveType.setMaxDaysPerYear(dto.getMaxDaysPerYear());
        leaveType.setCarryForwardAllowed(dto.getCarryForwardAllowed());
        leaveType.setMaxCarryForwardDays(dto.getMaxCarryForwardDays());
        leaveType.setEncashmentAllowed(dto.getEncashmentAllowed());
        leaveType.setIsPaidLeave(dto.getIsPaidLeave());
        leaveType.setStatus(dto.getStatus());
        return leaveType;
    }
}