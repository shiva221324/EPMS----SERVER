//package com.epms.payroll.Controller;
//
//import com.epms.payroll.Dto.LeaveBalanceDto;
//import com.epms.payroll.Entities.Employee;
//import com.epms.payroll.Entities.LeaveBalance;
//import com.epms.payroll.Entities.LeaveType;
//import com.epms.payroll.Exception.codes.ErrorCode;
//import com.epms.payroll.Exception.custom.BusinessException;
//import com.epms.payroll.Exception.custom.ResourceNotFoundException;
//import com.epms.payroll.Repositories.EmployeeRepository;
//import com.epms.payroll.Repositories.LeaveBalanceRepository;
//import com.epms.payroll.Repositories.LeaveTypeRepository;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/leave-balances")
//public class LeaveBalanceController {
//
//    private final LeaveBalanceRepository leaveBalanceRepository;
//    private final EmployeeRepository employeeRepository;
//    private final LeaveTypeRepository leaveTypeRepository;
//
//    public LeaveBalanceController(LeaveBalanceRepository leaveBalanceRepository,
//                                  EmployeeRepository employeeRepository,
//                                  LeaveTypeRepository leaveTypeRepository) {
//        this.leaveBalanceRepository = leaveBalanceRepository;
//        this.employeeRepository = employeeRepository;
//        this.leaveTypeRepository = leaveTypeRepository;
//    }
//
//    @PostMapping
//    public ResponseEntity<LeaveBalanceDto> createLeaveBalance(@Valid @RequestBody LeaveBalanceDto leaveBalanceDto) {
//        validateLeaveBalanceDto(leaveBalanceDto);
//
//        // Validate Employee
//        Employee employee = employeeRepository.findById(leaveBalanceDto.getEmployeeId())
//                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + leaveBalanceDto.getEmployeeId()));
//
//        // Validate LeaveType
//        LeaveType leaveType = leaveTypeRepository.findById(leaveBalanceDto.getLeaveTypeId())
//                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + leaveBalanceDto.getLeaveTypeId()));
//
//        // Check for duplicate leave balance (same employee, leave type, and year)
//        if (leaveBalanceRepository.existsByEmployee_EmployeeIdAndLeaveType_LeaveTypeIdAndYear(
//                leaveBalanceDto.getEmployeeId(), leaveBalanceDto.getLeaveTypeId(), leaveBalanceDto.getYear())) {
//            throw new BusinessException(
//                    HttpStatus.CONFLICT,
//                    ErrorCode.CONFLICT,
//                    "Leave balance already exists for employee ID " + leaveBalanceDto.getEmployeeId() +
//                            ", leave type ID " + leaveBalanceDto.getLeaveTypeId() + ", and year " + leaveBalanceDto.getYear()
//            );
//        }
//
//        LeaveBalance leaveBalance = mapToEntity(leaveBalanceDto);
//        leaveBalance.setEmployee(employee);
//        leaveBalance.setLeaveType(leaveType);
//
//        LeaveBalance savedLeaveBalance = leaveBalanceRepository.save(leaveBalance);
//        return new ResponseEntity<>(mapToDto(savedLeaveBalance), HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<LeaveBalanceDto> getLeaveBalanceById(@PathVariable Long id) {
//        LeaveBalance leaveBalance = leaveBalanceRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found with id: " + id));
//        return ResponseEntity.ok(mapToDto(leaveBalance));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<LeaveBalanceDto>> getAllLeaveBalances() {
//        List<LeaveBalanceDto> dtos = leaveBalanceRepository.findAll()
//                .stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(dtos);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<LeaveBalanceDto> updateLeaveBalance(@PathVariable Long id, @Valid @RequestBody LeaveBalanceDto leaveBalanceDto) {
//        validateLeaveBalanceDto(leaveBalanceDto);
//
//        LeaveBalance existingLeaveBalance = leaveBalanceRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found with id: " + id));
//
//        // Validate Employee (if provided)
//        if (leaveBalanceDto.getEmployeeId() != null) {
//            Employee employee = employeeRepository.findById(leaveBalanceDto.getEmployeeId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + leaveBalanceDto.getEmployeeId()));
//            existingLeaveBalance.setEmployee(employee);
//        }
//
//        // Validate LeaveType (if provided)
//        if (leaveBalanceDto.getLeaveTypeId() != null) {
//            LeaveType leaveType = leaveTypeRepository.findById(leaveBalanceDto.getLeaveTypeId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + leaveBalanceDto.getLeaveTypeId()));
//            existingLeaveBalance.setLeaveType(leaveType);
//        }
//
//        // Check for duplicate leave balance (excluding current record)
//        if (leaveBalanceDto.getEmployeeId() != null && leaveBalanceDto.getLeaveTypeId() != null && leaveBalanceDto.getYear() != null) {
//            if (!leaveBalanceDto.getEmployeeId().equals(existingLeaveBalance.getEmployee().getEmployeeId()) ||
//                    !leaveBalanceDto.getLeaveTypeId().equals(existingLeaveBalance.getLeaveType().getLeaveTypeId()) ||
//                    !leaveBalanceDto.getYear().equals(existingLeaveBalance.getYear())) {
//                if (leaveBalanceRepository.existsByEmployee_EmployeeIdAndLeaveType_LeaveTypeIdAndYear(
//                        leaveBalanceDto.getEmployeeId(), leaveBalanceDto.getLeaveTypeId(), leaveBalanceDto.getYear())) {
//                    throw new BusinessException(
//                            HttpStatus.CONFLICT,
//                            ErrorCode.CONFLICT,
//                            "Leave balance already exists for employee ID " + leaveBalanceDto.getEmployeeId() +
//                                    ", leave type ID " + leaveBalanceDto.getLeaveTypeId() + ", and year " + leaveBalanceDto.getYear()
//                    );
//                }
//            }
//        }
//
//        updateLeaveBalanceFields(existingLeaveBalance, leaveBalanceDto);
//        LeaveBalance updatedLeaveBalance = leaveBalanceRepository.save(existingLeaveBalance);
//        return ResponseEntity.ok(mapToDto(updatedLeaveBalance));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteLeaveBalance(@PathVariable Long id) {
//        if (!leaveBalanceRepository.existsById(id)) {
//            throw new ResourceNotFoundException("Leave balance not found with id: " + id);
//        }
//        leaveBalanceRepository.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // --- Helper Methods ---
//
//    private void validateLeaveBalanceDto(LeaveBalanceDto dto) {
//        if (dto.getEmployeeId() == null) {
//            throw new BusinessException(
//                    HttpStatus.BAD_REQUEST,
//                    ErrorCode.BAD_REQUEST,
//                    "Employee ID is required"
//            );
//        }
//        if (dto.getLeaveTypeId() == null) {
//            throw new BusinessException(
//                    HttpStatus.BAD_REQUEST,
//                    ErrorCode.BAD_REQUEST,
//                    "Leave type ID is required"
//            );
//        }
//        if (dto.getYear() == null) {
//            throw new BusinessException(
//                    HttpStatus.BAD_REQUEST,
//                    ErrorCode.BAD_REQUEST,
//                    "Year is required"
//            );
//        }
//        if (dto.getTotalDays() != null && dto.getTotalDays().compareTo(BigDecimal.ZERO) < 0) {
//            throw new BusinessException(
//                    HttpStatus.BAD_REQUEST,
//                    ErrorCode.BAD_REQUEST,
//                    "Total days cannot be negative"
//            );
//        }
//
//        if (dto.getUsedDays() != null && dto.getUsedDays().compareTo(BigDecimal.ZERO) < 0) {
//            throw new BusinessException(
//                    HttpStatus.BAD_REQUEST,
//                    ErrorCode.BAD_REQUEST,
//                    "Used days cannot be negative"
//            );
//        }
//
//        if (dto.getCarryForwardDays() != null && dto.getCarryForwardDays().compareTo(BigDecimal.ZERO) < 0) {
//            throw new BusinessException(
//                    HttpStatus.BAD_REQUEST,
//                    ErrorCode.BAD_REQUEST,
//                    "Carry forward days cannot be negative"
//            );
//        }
//
//        if (dto.getCurrentBalance() != null && dto.getCurrentBalance().compareTo(BigDecimal.ZERO) < 0) {
//            throw new BusinessException(
//                    HttpStatus.BAD_REQUEST,
//                    ErrorCode.BAD_REQUEST,
//                    "Current balance cannot be negative"
//            );
//        }
//
//        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
//            throw new BusinessException(
//                    HttpStatus.BAD_REQUEST,
//                    ErrorCode.BAD_REQUEST,
//                    "Status is required and cannot be empty"
//            );
//        }
//        // Validate consistency: currentBalance = totalDays - usedDays + carryForwardDays
//        if (dto.getTotalDays() != null && dto.getUsedDays() != null &&
//                dto.getCarryForwardDays() != null && dto.getCurrentBalance() != null) {
//
//            BigDecimal expectedBalance = dto.getTotalDays()
//                    .subtract(dto.getUsedDays())
//                    .add(dto.getCarryForwardDays());
//
//            if (dto.getCurrentBalance().compareTo(expectedBalance) != 0) {
//                throw new BusinessException(
//                        HttpStatus.BAD_REQUEST,
//                        ErrorCode.BAD_REQUEST,
//                        "Current balance must equal totalDays - usedDays + carryForwardDays"
//                );
//            }
//        }
//
//    }
//
//    private void updateLeaveBalanceFields(LeaveBalance existing, LeaveBalanceDto dto) {
//        existing.setTotalDays(dto.getTotalDays());
//        existing.setUsedDays(dto.getUsedDays());
//        existing.setCarryForwardDays(dto.getCarryForwardDays());
//        existing.setCurrentBalance(dto.getCurrentBalance());
//        existing.setYear(dto.getYear());
//        existing.setStatus(dto.getStatus());
//    }
//
//    private LeaveBalanceDto mapToDto(LeaveBalance leaveBalance) {
//        LeaveBalanceDto dto = new LeaveBalanceDto();
//        dto.setLeaveBalanceId(leaveBalance.getLeaveBalanceId());
//        dto.setEmployeeId(leaveBalance.getEmployee() != null ? leaveBalance.getEmployee().getEmployeeId() : null);
//        dto.setLeaveTypeId(leaveBalance.getLeaveType() != null ? leaveBalance.getLeaveType().getLeaveTypeId() : null);
//        dto.setTotalDays(leaveBalance.getTotalDays());
//        dto.setUsedDays(leaveBalance.getUsedDays());
//        dto.setCarryForwardDays(leaveBalance.getCarryForwardDays());
//        dto.setCurrentBalance(leaveBalance.getCurrentBalance());
//        dto.setYear(leaveBalance.getYear());
//        dto.setStatus(leaveBalance.getStatus());
//        dto.setCreatedAt(leaveBalance.getCreatedAt());
//        dto.setUpdatedAt(leaveBalance.getUpdatedAt());
//        return dto;
//    }
//
//    private LeaveBalance mapToEntity(LeaveBalanceDto dto) {
//        LeaveBalance leaveBalance = new LeaveBalance();
//        leaveBalance.setTotalDays(dto.getTotalDays());
//        leaveBalance.setUsedDays(dto.getUsedDays());
//        leaveBalance.setCarryForwardDays(dto.getCarryForwardDays());
//        leaveBalance.setCurrentBalance(dto.getCurrentBalance());
//        leaveBalance.setYear(dto.getYear());
//        leaveBalance.setStatus(dto.getStatus());
//        return leaveBalance;
//    }
//}