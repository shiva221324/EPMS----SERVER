package com.epms.payroll.Dto;

import lombok.Data;

@Data
public class DepartmentDto {
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private String description;
    private Long parentDepartmentId;
}