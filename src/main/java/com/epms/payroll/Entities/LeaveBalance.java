//package com.epms.payroll.Entities;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "LEAVE_BALANCE")
//public class LeaveBalance {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "leave_balance_id")
//    private Long leaveBalanceId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "employee_id", nullable = false)
//    private Employee employee;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "leave_type_id", nullable = false)
//    private LeaveType leaveType;
//
//    @Column(name = "total_days")
//    private BigDecimal totalDays;
//
//    @Column(name = "used_days")
//    private BigDecimal usedDays;
//
//    @Column(name = "carry_forward_days")
//    private BigDecimal carryForwardDays;
//
//    @Column(name = "current_balance")
//    private BigDecimal currentBalance;
//
//    @Column(name = "year")
//    private Integer year;
//
//    @Column(name = "status")
//    private String status;
//
//    @CreationTimestamp
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//}