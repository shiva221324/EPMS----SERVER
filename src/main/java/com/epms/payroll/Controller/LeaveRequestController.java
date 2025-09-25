package com.epms.payroll.Controller;

import com.epms.payroll.Dto.LeaveRequestDto;
import com.epms.payroll.Entities.Employee;
import com.epms.payroll.Entities.LeaveRequest;
import com.epms.payroll.Entities.LeaveType;
import com.epms.payroll.Exception.codes.ErrorCode;
import com.epms.payroll.Exception.custom.BusinessException;
import com.epms.payroll.Exception.custom.ResourceNotFoundException;
import com.epms.payroll.Repositories.EmployeeRepository;
import com.epms.payroll.Repositories.LeaveRequestRepository;
import com.epms.payroll.Repositories.LeaveTypeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveRequestController(LeaveRequestRepository leaveRequestRepository,
                                  EmployeeRepository employeeRepository,
                                  LeaveTypeRepository leaveTypeRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @PostMapping
    public ResponseEntity<LeaveRequestDto> createLeaveRequest(@Valid @RequestBody LeaveRequestDto dto) {
        validateLeaveRequestDto(dto);

        // Validate Employee
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));

        // Validate LeaveType
        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + dto.getLeaveTypeId()));

        // Validate AppliedBy (if provided)
        Employee appliedBy = null;
        if (dto.getAppliedById() != null) {
            appliedBy = employeeRepository.findById(dto.getAppliedById())
                    .orElseThrow(() -> new ResourceNotFoundException("AppliedBy employee not found with id: " + dto.getAppliedById()));
        }

        // Validate ApprovedBy (if provided)
        Employee approvedBy = null;
        if (dto.getApprovedById() != null) {
            approvedBy = employeeRepository.findById(dto.getApprovedById())
                    .orElseThrow(() -> new ResourceNotFoundException("ApprovedBy employee not found with id: " + dto.getApprovedById()));
        }

        LeaveRequest leaveRequest = mapToEntity(dto);
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setAppliedBy(appliedBy);
        leaveRequest.setApprovedBy(approvedBy);
        leaveRequest.setAppliedAt(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return new ResponseEntity<>(mapToDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDto> getLeaveRequestById(@PathVariable Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        return ResponseEntity.ok(mapToDto(leaveRequest));
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequestDto>> getAllLeaveRequests() {
        List<LeaveRequestDto> dtos = leaveRequestRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequestDto> updateLeaveRequest(@PathVariable Long id, @Valid @RequestBody LeaveRequestDto dto) {
        validateLeaveRequestDto(dto);

        LeaveRequest existing = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));

        // Validate Employee (if provided)
        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));
            existing.setEmployee(employee);
        }

        // Validate LeaveType (if provided)
        if (dto.getLeaveTypeId() != null) {
            LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + dto.getLeaveTypeId()));
            existing.setLeaveType(leaveType);
        }

        // Validate AppliedBy (if provided)
        if (dto.getAppliedById() != null) {
            Employee appliedBy = employeeRepository.findById(dto.getAppliedById())
                    .orElseThrow(() -> new ResourceNotFoundException("AppliedBy employee not found with id: " + dto.getAppliedById()));
            existing.setAppliedBy(appliedBy);
        } else {
            existing.setAppliedBy(null);
        }

        // Validate ApprovedBy (if provided)
        if (dto.getApprovedById() != null) {
            Employee approvedBy = employeeRepository.findById(dto.getApprovedById())
                    .orElseThrow(() -> new ResourceNotFoundException("ApprovedBy employee not found with id: " + dto.getApprovedById()));
            existing.setApprovedBy(approvedBy);
        } else {
            existing.setApprovedBy(null);
        }

        updateLeaveRequestFields(existing, dto);
        LeaveRequest updated = leaveRequestRepository.save(existing);
        return ResponseEntity.ok(mapToDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave request not found with id: " + id);
        }
        leaveRequestRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helper Methods ---

    private void validateLeaveRequestDto(LeaveRequestDto dto) {
        if (dto.getEmployeeId() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Employee ID is required"
            );
        }
        if (dto.getLeaveTypeId() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Leave type ID is required"
            );
        }
        if (dto.getStartDate() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Start date is required"
            );
        }
        if (dto.getEndDate() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "End date is required"
            );
        }
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Start date must be before or equal to end date"
            );
        }
        if (dto.getTotalDays() != null && dto.getTotalDays().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Total days cannot be negative"
            );
        }

        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "Status is required and cannot be empty"
            );
        }
        // Add more validations (e.g., valid status values) as needed
    }

    private void updateLeaveRequestFields(LeaveRequest existing, LeaveRequestDto dto) {
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setTotalDays(dto.getTotalDays());
        existing.setReason(dto.getReason());
        existing.setStatus(dto.getStatus());
        existing.setAppliedAt(dto.getAppliedAt());
        existing.setApprovedAt(dto.getApprovedAt());
        existing.setApprovalRemarks(dto.getApprovalRemarks());
    }

    private LeaveRequestDto mapToDto(LeaveRequest leaveRequest) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setLeaveRequestId(leaveRequest.getLeaveRequestId());
        dto.setEmployeeId(leaveRequest.getEmployee() != null ? leaveRequest.getEmployee().getEmployeeId() : null);
        dto.setLeaveTypeId(leaveRequest.getLeaveType() != null ? leaveRequest.getLeaveType().getLeaveTypeId() : null);
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setTotalDays(leaveRequest.getTotalDays());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus());
        dto.setAppliedById(leaveRequest.getAppliedBy() != null ? leaveRequest.getAppliedBy().getEmployeeId() : null);
        dto.setAppliedAt(leaveRequest.getAppliedAt());
        dto.setApprovedById(leaveRequest.getApprovedBy() != null ? leaveRequest.getApprovedBy().getEmployeeId() : null);
        dto.setApprovedAt(leaveRequest.getApprovedAt());
        dto.setApprovalRemarks(leaveRequest.getApprovalRemarks());
        dto.setCreatedAt(leaveRequest.getCreatedAt());
        dto.setUpdatedAt(leaveRequest.getUpdatedAt());
        return dto;
    }

    private LeaveRequest mapToEntity(LeaveRequestDto dto) {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStartDate(dto.getStartDate());
        leaveRequest.setEndDate(dto.getEndDate());
        leaveRequest.setTotalDays(dto.getTotalDays());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setStatus(dto.getStatus());
        leaveRequest.setAppliedAt(dto.getAppliedAt());
        leaveRequest.setApprovedAt(dto.getApprovedAt());
        leaveRequest.setApprovalRemarks(dto.getApprovalRemarks());
        return leaveRequest;
    }
}