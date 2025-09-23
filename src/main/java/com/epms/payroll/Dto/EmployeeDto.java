package com.epms.payroll.Dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeDto {
    private Long employeeId;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String maritalStatus;
    private String bloodGroup;
    private String panNumber;
    private String aadhaarNumber;
    private String passportNumber;
    private String drivingLicense;
    private String addressPermanent;
    private String addressCurrent;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private String photoPath;
    private String status;
    private Long departmentId;
    private Long designationId;
    private Long branchId;
    private Long userId;
    private String username; // For User relationship
    private String password; // For user creation (input only)
    private Long createdById;
    private Long updatedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}