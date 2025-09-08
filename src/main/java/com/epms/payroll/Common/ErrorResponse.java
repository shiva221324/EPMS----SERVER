package com.epms.payroll.Common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import com.project.hms.Exception.ApiSubError;
@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String code;       // standardized error code
    private String error;      // short error title
    private String message;    // developer-friendly or localized message
    private String path;       // request path
    private String traceId;    // correlation id
    private List<ApiSubError> subErrors; // list of detailed issues
}
