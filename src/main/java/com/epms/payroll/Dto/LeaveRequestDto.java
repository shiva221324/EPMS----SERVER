package com.epms.payroll.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaveRequestDto {
    private Long leaveRequestId;
    private Long employeeId;
    private Long leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalDays;
    private String reason;
    private String status;
    private Long appliedById;
    private LocalDateTime appliedAt;
    private Long approvedById;
    private LocalDateTime approvedAt;
    private String approvalRemarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}