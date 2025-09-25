package com.epms.payroll.Dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttendanceSummaryDto {
    private BigDecimal totalHoursWorked;
    private BigDecimal totalOvertimeHours;
    long presentDays;
    private long absentDays;
    private long totalDays;
}