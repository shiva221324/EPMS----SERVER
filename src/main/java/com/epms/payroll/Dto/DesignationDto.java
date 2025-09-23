package com.epms.payroll.Dto;

import lombok.Data;

@Data
public class DesignationDto {
    private Long designationId;
    private String designationName;
    private String designationCode;
    private String level;
}