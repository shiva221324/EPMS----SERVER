package com.epms.payroll.Controller;

import com.epms.payroll.Dto.AttendanceLogDto;
import com.epms.payroll.Entities.AttendanceLog;
import com.epms.payroll.Entities.Employee;
import com.epms.payroll.Repositories.AttendanceLogRepository;
import com.epms.payroll.Repositories.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance-logs")
public class AttendanceLogController {

    private final AttendanceLogRepository attendanceLogRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceLogController(AttendanceLogRepository attendanceLogRepository, EmployeeRepository employeeRepository) {
        this.attendanceLogRepository = attendanceLogRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping
    public ResponseEntity<AttendanceLogDto> createAttendanceLog(@RequestBody AttendanceLogDto logDto) {
        AttendanceLog log = mapToEntity(logDto);
        AttendanceLog savedLog = attendanceLogRepository.save(log);
        return new ResponseEntity<>(mapToDto(savedLog), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceLogDto> getAttendanceLogById(@PathVariable Long id) {
        return attendanceLogRepository.findById(id)
                .map(log -> ResponseEntity.ok(mapToDto(log)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AttendanceLogDto>> getAllAttendanceLogs() {
        List<AttendanceLog> logs = attendanceLogRepository.findAll();
        List<AttendanceLogDto> dtos = logs.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceLogDto>> getAttendanceLogsByEmployee(@PathVariable Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            return ResponseEntity.notFound().build();
        }
        List<AttendanceLog> logs = attendanceLogRepository.findByEmployee_EmployeeId(employeeId);
        List<AttendanceLogDto> dtos = logs.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceLogDto> updateAttendanceLog(@PathVariable Long id, @RequestBody AttendanceLogDto logDto) {
        return attendanceLogRepository.findById(id)
                .map(existingLog -> {
                    // Update the existing log's fields
                    existingLog.setAttendanceDate(logDto.getAttendanceDate());
                    existingLog.setCheckInTime(logDto.getCheckInTime());
                    existingLog.setCheckOutTime(logDto.getCheckOutTime());
                    existingLog.setHoursWorked(logDto.getHoursWorked());
                    existingLog.setAttendanceStatus(logDto.getAttendanceStatus());

                    // Re-assign employee and approver if their IDs are provided
                    if (logDto.getEmployeeId() != null) {
                        Employee employee = employeeRepository.findById(logDto.getEmployeeId())
                                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + logDto.getEmployeeId()));
                        existingLog.setEmployee(employee);
                    }
                    if (logDto.getApprovedById() != null) {
                        Employee approver = employeeRepository.findById(logDto.getApprovedById())
                                .orElseThrow(() -> new RuntimeException("Approver employee not found with id: " + logDto.getApprovedById()));
                        existingLog.setApprovedBy(approver);
                    } else {
                        existingLog.setApprovedBy(null);
                    }

                    AttendanceLog updatedLog = attendanceLogRepository.save(existingLog);
                    return ResponseEntity.ok(mapToDto(updatedLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendanceLog(@PathVariable Long id) {
        if (!attendanceLogRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        attendanceLogRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helper Methods for DTO/Entity Mapping ---

    private AttendanceLogDto mapToDto(AttendanceLog log) {
        AttendanceLogDto dto = new AttendanceLogDto();
        dto.setAttendanceId(log.getAttendanceId());
        dto.setAttendanceDate(log.getAttendanceDate());
        dto.setCheckInTime(log.getCheckInTime());
        dto.setCheckOutTime(log.getCheckOutTime());
        dto.setHoursWorked(log.getHoursWorked());
        dto.setAttendanceStatus(log.getAttendanceStatus());

        if (log.getEmployee() != null) {
            dto.setEmployeeId(log.getEmployee().getEmployeeId());
        }
        if (log.getApprovedBy() != null) {
            dto.setApprovedById(log.getApprovedBy().getEmployeeId());
        }
        return dto;
    }

    private AttendanceLog mapToEntity(AttendanceLogDto dto) {
        AttendanceLog log = new AttendanceLog();
        log.setAttendanceDate(dto.getAttendanceDate());
        log.setCheckInTime(dto.getCheckInTime());
        log.setCheckOutTime(dto.getCheckOutTime());
        log.setHoursWorked(dto.getHoursWorked());
        log.setAttendanceStatus(dto.getAttendanceStatus());

        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));
            log.setEmployee(employee);
        }
        if (dto.getApprovedById() != null) {
            Employee approver = employeeRepository.findById(dto.getApprovedById())
                    .orElseThrow(() -> new RuntimeException("Approver employee not found with id: " + dto.getApprovedById()));
            log.setApprovedBy(approver);
        }
        return log;
    }
}