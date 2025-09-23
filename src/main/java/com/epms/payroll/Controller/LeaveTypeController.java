package com.epms.payroll.Controller;


import com.epms.payroll.Dto.LeaveTypeDto;
import com.epms.payroll.Entities.LeaveType;
import com.epms.payroll.Repositories.LeaveTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<LeaveTypeDto> createLeaveType(@RequestBody LeaveTypeDto leaveTypeDto) {
        LeaveType leaveType = mapToEntity(leaveTypeDto);
        LeaveType savedLeaveType = leaveTypeRepository.save(leaveType);
        return new ResponseEntity<>(mapToDto(savedLeaveType), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> getLeaveTypeById(@PathVariable Long id) {
        return leaveTypeRepository.findById(id)
                .map(leaveType -> ResponseEntity.ok(mapToDto(leaveType)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<LeaveTypeDto>> getAllLeaveTypes() {
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        List<LeaveTypeDto> leaveTypeDtos = leaveTypes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaveTypeDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> updateLeaveType(@PathVariable Long id, @RequestBody LeaveTypeDto leaveTypeDto) {
        return leaveTypeRepository.findById(id)
                .map(existingLeaveType -> {
                    existingLeaveType.setLeaveName(leaveTypeDto.getLeaveName());
                    existingLeaveType.setLeaveCode(leaveTypeDto.getLeaveCode());
                    existingLeaveType.setLeaveCategory(leaveTypeDto.getLeaveCategory());
                    existingLeaveType.setMaxDaysPerYear(leaveTypeDto.getMaxDaysPerYear());
                    existingLeaveType.setCarryForwardAllowed(leaveTypeDto.getCarryForwardAllowed());
                    existingLeaveType.setMaxCarryForwardDays(leaveTypeDto.getMaxCarryForwardDays());
                    existingLeaveType.setEncashmentAllowed(leaveTypeDto.getEncashmentAllowed());
                    existingLeaveType.setIsPaidLeave(leaveTypeDto.getIsPaidLeave());
                    existingLeaveType.setStatus(leaveTypeDto.getStatus());
                    LeaveType updatedLeaveType = leaveTypeRepository.save(existingLeaveType);
                    return ResponseEntity.ok(mapToDto(updatedLeaveType));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        if (!leaveTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        leaveTypeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
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