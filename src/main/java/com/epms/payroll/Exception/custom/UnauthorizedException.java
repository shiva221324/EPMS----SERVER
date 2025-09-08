package com.epms.payroll.Exception.custom;


import com.epms.payroll.Exception.codes.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, message);
    }
}
