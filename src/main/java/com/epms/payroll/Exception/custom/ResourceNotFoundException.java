package com.epms.payroll.Exception.custom;


import com.epms.payroll.Exception.codes.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, message);
    }
}

