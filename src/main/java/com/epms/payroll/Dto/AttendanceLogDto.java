package com.epms.payroll.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceLogDto {
    private Long attendanceId;
    private Long employeeId;
    private LocalDate attendanceDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private BigDecimal hoursWorked;
    private BigDecimal overtimeHours;
    private String attendanceStatus;
    private String shiftType;
    private String workLocation;
    private String remarks;
    private Long approvedById;
}