package com.epms.payroll.Dto;

import lombok.Data;

@Data
public class BranchDto {
    private Long branchId;
    private String branchName;
    private String branchCode;
    private String city;
    private String state;
    private String country;
}