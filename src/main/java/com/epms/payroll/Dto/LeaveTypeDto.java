package com.epms.payroll.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LeaveTypeDto {
    private Long leaveTypeId;
    private String leaveName;
    private String leaveCode;
    private String leaveCategory;
    private BigDecimal maxDaysPerYear;
    private String carryForwardAllowed;
    private BigDecimal maxCarryForwardDays;
    private String encashmentAllowed;
    private String isPaidLeave;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}