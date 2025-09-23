
package com.epms.payroll.Common;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;         // HTTP status code
    private String code;        // custom business error code (optional)
    private String message;     // success/error message
    private T data;             // response payload (if any)

    // Flexible success factory method
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return success(200, message, data);
    }

    // Error response
    public static <T> ApiResponse<T> error(int status, String code, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}

