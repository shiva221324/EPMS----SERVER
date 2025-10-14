//package com.epms.payroll.Entities;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.time.LocalDateTime;
//import java.util.Set;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "DEPARTMENT")
//public class Department {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "department_id")
//    private Long departmentId;
//
//    @Column(name = "department_name", unique = true, nullable = false)
//    private String departmentName;
//
//    @Column(name = "department_code", unique = true, nullable = false)
//    private String departmentCode;
//
//    @Column(name = "description", columnDefinition = "TEXT")
//    private String description;
//
//    // Self-referencing relationship for parent department
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_department_id")
//    private Department parentDepartment;
//
//    @OneToMany(mappedBy = "parentDepartment")
//    private Set<Department> childDepartments;
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