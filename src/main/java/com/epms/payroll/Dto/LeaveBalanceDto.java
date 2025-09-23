package com.epms.payroll.Dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LeaveBalanceDto {
    private Long leaveBalanceId;
    private Long employeeId;
    private Long leaveTypeId;
    private BigDecimal totalDays;
    private BigDecimal usedDays;
    private BigDecimal carryForwardDays;
    private BigDecimal currentBalance;
    private Integer year;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}