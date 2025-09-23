package com.epms.payroll.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LEAVE_TYPE")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_type_id")
    private Long leaveTypeId;

    @Column(name = "leave_name", unique = true, nullable = false)
    private String leaveName;

    @Column(name = "leave_code", unique = true, nullable = false)
    private String leaveCode;

    @Column(name = "leave_category")
    private String leaveCategory;

    @Column(name = "max_days_per_year")
    private BigDecimal maxDaysPerYear;

    @Column(name = "carry_forward_allowed")
    private String carryForwardAllowed;

    @Column(name = "max_carry_forward_days")
    private BigDecimal maxCarryForwardDays;

    @Column(name = "encashment_allowed")
    private String encashmentAllowed;

    @Column(name = "is_paid_leave")
    private String isPaidLeave;

    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}