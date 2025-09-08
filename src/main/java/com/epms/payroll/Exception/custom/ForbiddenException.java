package com.epms.payroll.Exception.custom;

import com.epms.payroll.Exception.codes.ErrorCode;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, message);
    }
}