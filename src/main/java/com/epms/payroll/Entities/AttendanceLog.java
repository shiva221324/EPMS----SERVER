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
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "ATTENDANCE_LOG")
//public class AttendanceLog {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "attendance_id")
//    private Long attendanceId;
//
//    // Foreign Key to Employee
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "employee_id", nullable = false)
//    private Employee employee;
//
//    @Column(name = "attendance_date")
//    private LocalDate attendanceDate;
//
//    @Column(name = "check_in_time")
//    private LocalTime checkInTime;
//
//    @Column(name = "check_out_time")
//    private LocalTime checkOutTime;
//
//    @Column(name = "hours_worked", precision = 5, scale = 2)
//    private BigDecimal hoursWorked;
//
//    @Column(name = "overtime_hours", precision = 5, scale = 2)
//    private BigDecimal overtimeHours;
//
//    @Column(name = "attendance_status")
//    private String attendanceStatus;
//
//    @Column(name = "shift_type")
//    private String shiftType;
//
//    @Column(name = "work_location")
//    private String workLocation;
//
//    @Column(name = "remarks")
//    private String remarks;
//
//    // Foreign Key to another Employee (Manager/Approver)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "approved_by")
//    private Employee approvedBy;
//
//    @Column(name = "approved_at")
//    private LocalDateTime approvedAt;
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