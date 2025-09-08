package com.epms.payroll.Exception.custom;

import com.epms.payroll.Exception.codes.ErrorCode;
import org.springframework.http.HttpStatus;

public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message);
    }
}
