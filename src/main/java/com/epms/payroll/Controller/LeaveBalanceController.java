package com.epms.payroll.Controller;


import com.epms.payroll.Dto.LeaveBalanceDto;
import com.epms.payroll.Entities.Employee;
import com.epms.payroll.Entities.LeaveBalance;
import com.epms.payroll.Entities.LeaveType;
import com.epms.payroll.Repositories.EmployeeRepository;
import com.epms.payroll.Repositories.LeaveBalanceRepository;
import com.epms.payroll.Repositories.LeaveTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave-balances")
public class LeaveBalanceController {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveBalanceController(LeaveBalanceRepository leaveBalanceRepository,
                                  EmployeeRepository employeeRepository,
                                  LeaveTypeRepository leaveTypeRepository) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @PostMapping
    public ResponseEntity<LeaveBalanceDto> createLeaveBalance(@RequestBody LeaveBalanceDto leaveBalanceDto) {
        LeaveBalance leaveBalance = mapToEntity(leaveBalanceDto);
        LeaveBalance savedLeaveBalance = leaveBalanceRepository.save(leaveBalance);
        return new ResponseEntity<>(mapToDto(savedLeaveBalance), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveBalanceDto> getLeaveBalanceById(@PathVariable Long id) {
        return leaveBalanceRepository.findById(id)
                .map(leaveBalance -> ResponseEntity.ok(mapToDto(leaveBalance)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<LeaveBalanceDto>> getAllLeaveBalances() {
        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findAll();
        List<LeaveBalanceDto> leaveBalanceDtos = leaveBalances.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaveBalanceDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveBalanceDto> updateLeaveBalance(@PathVariable Long id, @RequestBody LeaveBalanceDto leaveBalanceDto) {
        return leaveBalanceRepository.findById(id)
                .map(existingLeaveBalance -> {
                    existingLeaveBalance.setTotalDays(leaveBalanceDto.getTotalDays());
                    existingLeaveBalance.setUsedDays(leaveBalanceDto.getUsedDays());
                    existingLeaveBalance.setCarryForwardDays(leaveBalanceDto.getCarryForwardDays());
                    existingLeaveBalance.setCurrentBalance(leaveBalanceDto.getCurrentBalance());
                    existingLeaveBalance.setYear(leaveBalanceDto.getYear());
                    existingLeaveBalance.setStatus(leaveBalanceDto.getStatus());
                    if (leaveBalanceDto.getEmployeeId() != null) {
                        Employee employee = employeeRepository.findById(leaveBalanceDto.getEmployeeId())
                                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + leaveBalanceDto.getEmployeeId()));
                        existingLeaveBalance.setEmployee(employee);
                    }
                    if (leaveBalanceDto.getLeaveTypeId() != null) {
                        LeaveType leaveType = leaveTypeRepository.findById(leaveBalanceDto.getLeaveTypeId())
                                .orElseThrow(() -> new RuntimeException("LeaveType not found with id: " + leaveBalanceDto.getLeaveTypeId()));
                        existingLeaveBalance.setLeaveType(leaveType);
                    }
                    LeaveBalance updatedLeaveBalance = leaveBalanceRepository.save(existingLeaveBalance);
                    return ResponseEntity.ok(mapToDto(updatedLeaveBalance));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveBalance(@PathVariable Long id) {
        if (!leaveBalanceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveBalanceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private LeaveBalanceDto mapToDto(LeaveBalance leaveBalance) {
        LeaveBalanceDto dto = new LeaveBalanceDto();
        dto.setLeaveBalanceId(leaveBalance.getLeaveBalanceId());
        dto.setEmployeeId(leaveBalance.getEmployee() != null ? leaveBalance.getEmployee().getEmployeeId() : null);
        dto.setLeaveTypeId(leaveBalance.getLeaveType() != null ? leaveBalance.getLeaveType().getLeaveTypeId() : null);
        dto.setTotalDays(leaveBalance.getTotalDays());
        dto.setUsedDays(leaveBalance.getUsedDays());
        dto.setCarryForwardDays(leaveBalance.getCarryForwardDays());
        dto.setCurrentBalance(leaveBalance.getCurrentBalance());
        dto.setYear(leaveBalance.getYear());
        dto.setStatus(leaveBalance.getStatus());
        dto.setCreatedAt(leaveBalance.getCreatedAt());
        dto.setUpdatedAt(leaveBalance.getUpdatedAt());
        return dto;
    }

    private LeaveBalance mapToEntity(LeaveBalanceDto dto) {
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setTotalDays(dto.getTotalDays());
        leaveBalance.setUsedDays(dto.getUsedDays());
        leaveBalance.setCarryForwardDays(dto.getCarryForwardDays());
        leaveBalance.setCurrentBalance(dto.getCurrentBalance());
        leaveBalance.setYear(dto.getYear());
        leaveBalance.setStatus(dto.getStatus());
        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));
            leaveBalance.setEmployee(employee);
        }
        if (dto.getLeaveTypeId() != null) {
            LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                    .orElseThrow(() -> new RuntimeException("LeaveType not found with id: " + dto.getLeaveTypeId()));
            leaveBalance.setLeaveType(leaveType);
        }
        return leaveBalance;
    }
}
