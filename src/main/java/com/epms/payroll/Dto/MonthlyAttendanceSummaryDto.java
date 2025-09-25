package com.epms.payroll.Dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlyAttendanceSummaryDto {
    private int month;
    private BigDecimal totalHoursWorked;
    private BigDecimal totalOvertimeHours;
    private long presentDays;
    private long absentDays;
    private long totalDays;
}