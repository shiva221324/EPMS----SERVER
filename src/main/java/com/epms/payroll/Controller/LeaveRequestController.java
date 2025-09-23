package com.epms.payroll.Controller;

import com.epms.payroll.Dto.LeaveRequestDto;
import com.epms.payroll.Entities.Employee;
import com.epms.payroll.Entities.LeaveRequest;
import com.epms.payroll.Entities.LeaveType;
import com.epms.payroll.Repositories.EmployeeRepository;
import com.epms.payroll.Repositories.LeaveRequestRepository;
import com.epms.payroll.Repositories.LeaveTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<LeaveRequestDto> createLeaveRequest(@RequestBody LeaveRequestDto leaveRequestDto) {
        LeaveRequest leaveRequest = mapToEntity(leaveRequestDto);
        leaveRequest.setAppliedAt(LocalDateTime.now());
        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        return new ResponseEntity<>(mapToDto(savedLeaveRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDto> getLeaveRequestById(@PathVariable Long id) {
        return leaveRequestRepository.findById(id)
                .map(leaveRequest -> ResponseEntity.ok(mapToDto(leaveRequest)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequestDto>> getAllLeaveRequests() {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
        List<LeaveRequestDto> leaveRequestDtos = leaveRequests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaveRequestDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequestDto> updateLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequestDto leaveRequestDto) {
        return leaveRequestRepository.findById(id)
                .map(existingLeaveRequest -> {
                    existingLeaveRequest.setStartDate(leaveRequestDto.getStartDate());
                    existingLeaveRequest.setEndDate(leaveRequestDto.getEndDate());
                    existingLeaveRequest.setTotalDays(leaveRequestDto.getTotalDays());
                    existingLeaveRequest.setReason(leaveRequestDto.getReason());
                    existingLeaveRequest.setStatus(leaveRequestDto.getStatus());
                    existingLeaveRequest.setAppliedAt(leaveRequestDto.getAppliedAt());
                    existingLeaveRequest.setApprovedAt(leaveRequestDto.getApprovedAt());
                    existingLeaveRequest.setApprovalRemarks(leaveRequestDto.getApprovalRemarks());
                    if (leaveRequestDto.getEmployeeId() != null) {
                        Employee employee = employeeRepository.findById(leaveRequestDto.getEmployeeId())
                                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + leaveRequestDto.getEmployeeId()));
                        existingLeaveRequest.setEmployee(employee);
                    }
                    if (leaveRequestDto.getLeaveTypeId() != null) {
                        LeaveType leaveType = leaveTypeRepository.findById(leaveRequestDto.getLeaveTypeId())
                                .orElseThrow(() -> new RuntimeException("LeaveType not found with id: " + leaveRequestDto.getLeaveTypeId()));
                        existingLeaveRequest.setLeaveType(leaveType);
                    }
                    if (leaveRequestDto.getAppliedById() != null) {
                        Employee appliedBy = employeeRepository.findById(leaveRequestDto.getAppliedById())
                                .orElseThrow(() -> new RuntimeException("AppliedBy Employee not found with id: " + leaveRequestDto.getAppliedById()));
                        existingLeaveRequest.setAppliedBy(appliedBy);
                    }
                    if (leaveRequestDto.getApprovedById() != null) {
                        Employee approvedBy = employeeRepository.findById(leaveRequestDto.getApprovedById())
                                .orElseThrow(() -> new RuntimeException("ApprovedBy Employee not found with id: " + leaveRequestDto.getApprovedById()));
                        existingLeaveRequest.setApprovedBy(approvedBy);
                    }
                    LeaveRequest updatedLeaveRequest = leaveRequestRepository.save(existingLeaveRequest);
                    return ResponseEntity.ok(mapToDto(updatedLeaveRequest));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveRequestRepository.deleteById(id);
        return ResponseEntity.noContent().build();
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
        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));
            leaveRequest.setEmployee(employee);
        }
        if (dto.getLeaveTypeId() != null) {
            LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                    .orElseThrow(() -> new RuntimeException("LeaveType not found with id: " + dto.getLeaveTypeId()));
            leaveRequest.setLeaveType(leaveType);
        }
        if (dto.getAppliedById() != null) {
            Employee appliedBy = employeeRepository.findById(dto.getAppliedById())
                    .orElseThrow(() -> new RuntimeException("AppliedBy Employee not found with id: " + dto.getAppliedById()));
            leaveRequest.setAppliedBy(appliedBy);
        }
        if (dto.getApprovedById() != null) {
            Employee approvedBy = employeeRepository.findById(dto.getApprovedById())
                    .orElseThrow(() -> new RuntimeException("ApprovedBy Employee not found with id: " + dto.getApprovedById()));
            leaveRequest.setApprovedBy(approvedBy);
        }
        return leaveRequest;
    }
}