package com.epms.payroll.Exception.codes;

public enum ErrorCode {

    // 4xx - Client Errors
    RESOURCE_NOT_FOUND("RES-404", "Resource Not Found"),
    BAD_REQUEST("REQ-400", "Bad Request"),
    VALIDATION_ERROR("VAL-400", "Validation Error"),
    UNAUTHORIZED("AUTH-401", "Unauthorized"),
    FORBIDDEN("AUTH-403", "Forbidden"),
    METHOD_NOT_ALLOWED("REQ-405", "Method Not Allowed"),
    CONFLICT("REQ-409", "Conflict"),

    // 5xx - Server Errors
    INTERNAL_ERROR("SYS-500", "Internal Server Error"),
    SERVICE_UNAVAILABLE("SYS-503", "Service Unavailable"),

    // Business-specific errors
    BUSINESS_ERROR("BUS-409", "Business Rule Violation");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
