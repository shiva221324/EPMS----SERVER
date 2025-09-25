package com.epms.payroll.Controller;

import com.epms.payroll.Dto.AttendanceLogDto;
import com.epms.payroll.Dto.AttendanceSummaryDto;
import com.epms.payroll.Dto.MonthlyAttendanceSummaryDto;
import com.epms.payroll.Entities.AttendanceLog;
import com.epms.payroll.Entities.Employee;
import com.epms.payroll.Exception.custom.ResourceNotFoundException;
import com.epms.payroll.Exception.custom.BusinessException;
import com.epms.payroll.Exception.codes.ErrorCode;
import com.epms.payroll.Repositories.AttendanceLogRepository;
import com.epms.payroll.Repositories.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
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
        validateAttendanceLogDto(logDto); // Validate input
        AttendanceLog log = mapToEntity(logDto);
        AttendanceLog savedLog = attendanceLogRepository.save(log);
        return new ResponseEntity<>(mapToDto(savedLog), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceLogDto> getAttendanceLogById(@PathVariable Long id) {
        AttendanceLog log = attendanceLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance log not found with id: " + id));
        return ResponseEntity.ok(mapToDto(log));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceLogDto>> getAllAttendanceLogs() {
        List<AttendanceLog> logs = attendanceLogRepository.findAll();
        List<AttendanceLogDto> dtos = logs.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceLogDto>> getAttendanceLogsByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        validateEmployeeExists(employeeId);
        validateYearAndMonth(year, month);

        List<AttendanceLog> logs = getAttendanceLogs(employeeId, year, month);
        List<AttendanceLogDto> dtos = logs.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceLogDto> updateAttendanceLog(@PathVariable Long id, @RequestBody AttendanceLogDto logDto) {
        validateAttendanceLogDto(logDto);
        AttendanceLog existingLog = attendanceLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance log not found with id: " + id));

        updateLogFields(existingLog, logDto);
        AttendanceLog updatedLog = attendanceLogRepository.save(existingLog);
        return ResponseEntity.ok(mapToDto(updatedLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendanceLog(@PathVariable Long id) {
        if (!attendanceLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance log not found with id: " + id);
        }
        attendanceLogRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}/summary")
    public ResponseEntity<AttendanceSummaryDto> getAttendanceSummary(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        validateEmployeeExists(employeeId);
        validateYearAndMonth(year, month);

        List<AttendanceLog> logs = getAttendanceLogs(employeeId, year, month);
        AttendanceSummaryDto summary = calculateSummary(logs);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/employee/{employeeId}/range")
    public ResponseEntity<List<AttendanceLogDto>> getAttendanceLogsByDateRange(
            @PathVariable Long employeeId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        validateEmployeeExists(employeeId);

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            if (start.isAfter(end)) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                        "Start date must be before or equal to end date");
            }

            List<AttendanceLog> logs = attendanceLogRepository.findByEmployee_EmployeeIdAndAttendanceDateBetween(
                    employeeId, start, end);
            List<AttendanceLogDto> dtos = logs.stream().map(this::mapToDto).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                    "Invalid date format or date range");
        }
    }

    @GetMapping("/employee/{employeeId}/status/{status}")
    public ResponseEntity<List<AttendanceLogDto>> getAttendanceLogsByStatus(
            @PathVariable Long employeeId,
            @PathVariable String status) {
        validateEmployeeExists(employeeId);
        String upperStatus = status.toUpperCase();
        if (!List.of("PRESENT", "ABSENT", "ON_LEAVE").contains(upperStatus)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                    "Invalid attendance status: " + status);
        }

        List<AttendanceLog> logs = attendanceLogRepository.findByEmployee_EmployeeIdAndAttendanceStatus(
                employeeId, upperStatus);
        List<AttendanceLogDto> dtos = logs.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/employee/{employeeId}/shift/{shiftType}")
    public ResponseEntity<List<AttendanceLogDto>> getAttendanceLogsByShiftType(
            @PathVariable Long employeeId,
            @PathVariable String shiftType) {
        validateEmployeeExists(employeeId);
        String upperShiftType = shiftType.toUpperCase();
        if (!List.of("DAY", "NIGHT", "FLEXIBLE").contains(upperShiftType)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                    "Invalid shift type: " + shiftType);
        }

        List<AttendanceLog> logs = attendanceLogRepository.findByEmployee_EmployeeIdAndShiftType(
                employeeId, upperShiftType);
        List<AttendanceLogDto> dtos = logs.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/employee/{employeeId}/location/{workLocation}")
    public ResponseEntity<List<AttendanceLogDto>> getAttendanceLogsByWorkLocation(
            @PathVariable Long employeeId,
            @PathVariable String workLocation) {
        validateEmployeeExists(employeeId);
        String upperWorkLocation = workLocation.toUpperCase();
        if (!List.of("OFFICE", "REMOTE", "CLIENT_SITE").contains(upperWorkLocation)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                    "Invalid work location: " + workLocation);
        }

        List<AttendanceLog> logs = attendanceLogRepository.findByEmployee_EmployeeIdAndWorkLocation(
                employeeId, upperWorkLocation);
        List<AttendanceLogDto> dtos = logs.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/employee/{employeeId}/trends")
    public ResponseEntity<List<MonthlyAttendanceSummaryDto>> getAttendanceTrends(
            @PathVariable Long employeeId,
            @RequestParam Integer year) {
        validateEmployeeExists(employeeId);
        if (year < 1900 || year > 9999) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                    "Invalid year: " + year);
        }

        List<MonthlyAttendanceSummaryDto> trends = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            List<AttendanceLog> logs = attendanceLogRepository.findByEmployee_EmployeeIdAndAttendanceDateBetween(
                    employeeId, startDate, endDate);
            MonthlyAttendanceSummaryDto monthlySummary = calculateMonthlySummary(logs, month);
            trends.add(monthlySummary);
        }

        return ResponseEntity.ok(trends);
    }

    // --- Helper Methods ---

    private void validateEmployeeExists(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
    }

    private void validateYearAndMonth(Integer year, Integer month) {
        if (year != null) {
            if (year < 1900 || year > 9999) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                        "Invalid year: " + year);
            }
            if (month != null && (month < 1 || month > 12)) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                        "Invalid month: " + month);
            }
        }
    }

    private List<AttendanceLog> getAttendanceLogs(Long employeeId, Integer year, Integer month) {
        if (year != null && month != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            return attendanceLogRepository.findByEmployee_EmployeeIdAndAttendanceDateBetween(employeeId, startDate, endDate);
        } else if (year != null) {
            return attendanceLogRepository.findByEmployee_EmployeeIdAndAttendanceDateYear(employeeId, year);
        } else {
            return attendanceLogRepository.findByEmployee_EmployeeId(employeeId);
        }
    }

    private void validateAttendanceLogDto(AttendanceLogDto logDto) {
        if (logDto.getEmployeeId() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                    "Employee ID is required");
        }
        if (logDto.getAttendanceDate() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST,
                    "Attendance date is required");
        }
        // Add more validations as needed (e.g., check for valid status, shift type, etc.)
    }

    private void updateLogFields(AttendanceLog existingLog, AttendanceLogDto logDto) {
        existingLog.setAttendanceDate(logDto.getAttendanceDate());
        existingLog.setCheckInTime(logDto.getCheckInTime());
        existingLog.setCheckOutTime(logDto.getCheckOutTime());
        existingLog.setHoursWorked(logDto.getHoursWorked());
        existingLog.setOvertimeHours(logDto.getOvertimeHours());
        existingLog.setAttendanceStatus(logDto.getAttendanceStatus());
        existingLog.setShiftType(logDto.getShiftType());
        existingLog.setWorkLocation(logDto.getWorkLocation());
        existingLog.setRemarks(logDto.getRemarks());

        if (logDto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(logDto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + logDto.getEmployeeId()));
            existingLog.setEmployee(employee);
        }
        if (logDto.getApprovedById() != null) {
            Employee approver = employeeRepository.findById(logDto.getApprovedById())
                    .orElseThrow(() -> new ResourceNotFoundException("Approver employee not found with id: " + logDto.getApprovedById()));
            existingLog.setApprovedBy(approver);
        } else {
            existingLog.setApprovedBy(null);
        }
    }

    private AttendanceLogDto mapToDto(AttendanceLog log) {
        AttendanceLogDto dto = new AttendanceLogDto();
        dto.setAttendanceId(log.getAttendanceId());
        dto.setAttendanceDate(log.getAttendanceDate());
        dto.setCheckInTime(log.getCheckInTime());
        dto.setCheckOutTime(log.getCheckOutTime());
        dto.setHoursWorked(log.getHoursWorked());
        dto.setOvertimeHours(log.getOvertimeHours());
        dto.setAttendanceStatus(log.getAttendanceStatus());
        dto.setShiftType(log.getShiftType());
        dto.setWorkLocation(log.getWorkLocation());
        dto.setRemarks(log.getRemarks());
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
        log.setOvertimeHours(dto.getOvertimeHours());
        log.setAttendanceStatus(dto.getAttendanceStatus());
        log.setShiftType(dto.getShiftType());
        log.setWorkLocation(dto.getWorkLocation());
        log.setRemarks(dto.getRemarks());
        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getEmployeeId()));
            log.setEmployee(employee);
        }
        if (dto.getApprovedById() != null) {
            Employee approver = employeeRepository.findById(dto.getApprovedById())
                    .orElseThrow(() -> new ResourceNotFoundException("Approver employee not found with id: " + dto.getApprovedById()));
            log.setApprovedBy(approver);
        }
        return log;
    }

    private AttendanceSummaryDto calculateSummary(List<AttendanceLog> logs) {
        AttendanceSummaryDto summary = new AttendanceSummaryDto();
        BigDecimal totalHoursWorked = BigDecimal.ZERO;
        BigDecimal totalOvertimeHours = BigDecimal.ZERO;
        long presentDays = 0;
        long absentDays = 0;

        for (AttendanceLog log : logs) {
            if (log.getHoursWorked() != null) {
                totalHoursWorked = totalHoursWorked.add(log.getHoursWorked());
            }
            if (log.getOvertimeHours() != null) {
                totalOvertimeHours = totalOvertimeHours.add(log.getOvertimeHours());
            }
            if ("PRESENT".equalsIgnoreCase(log.getAttendanceStatus())) {
                presentDays++;
            } else if ("ABSENT".equalsIgnoreCase(log.getAttendanceStatus())) {
                absentDays++;
            }
        }

        summary.setTotalHoursWorked(totalHoursWorked);
        summary.setTotalOvertimeHours(totalOvertimeHours);
        summary.setPresentDays(presentDays);
        summary.setAbsentDays(absentDays);
        summary.setTotalDays(presentDays + absentDays);
        return summary;
    }

    private MonthlyAttendanceSummaryDto calculateMonthlySummary(List<AttendanceLog> logs, int month) {
        MonthlyAttendanceSummaryDto summary = new MonthlyAttendanceSummaryDto();
        summary.setMonth(month);
        BigDecimal totalHoursWorked = BigDecimal.ZERO;
        BigDecimal totalOvertimeHours = BigDecimal.ZERO;
        long presentDays = 0;
        long absentDays = 0;

        for (AttendanceLog log : logs) {
            if (log.getHoursWorked() != null) {
                totalHoursWorked = totalHoursWorked.add(log.getHoursWorked());
            }
            if (log.getOvertimeHours() != null) {
                totalOvertimeHours = totalOvertimeHours.add(log.getOvertimeHours());
            }
            if ("PRESENT".equalsIgnoreCase(log.getAttendanceStatus())) {
                presentDays++;
            } else if ("ABSENT".equalsIgnoreCase(log.getAttendanceStatus())) {
                absentDays++;
            }
        }

        summary.setTotalHoursWorked(totalHoursWorked);
        summary.setTotalOvertimeHours(totalOvertimeHours);
        summary.setPresentDays(presentDays);
        summary.setAbsentDays(absentDays);
        summary.setTotalDays(presentDays + absentDays);
        return summary;
    }
}