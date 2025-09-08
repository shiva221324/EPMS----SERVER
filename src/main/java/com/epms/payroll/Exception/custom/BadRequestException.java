package com.epms.payroll.Exception.custom;

import com.epms.payroll.Exception.codes.ErrorCode;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {

        super(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST, message);
    }
}
