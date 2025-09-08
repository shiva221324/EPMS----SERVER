package com.project.hms.Exception;


import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class ApiSubError {
    private String field;
    private Object rejectedValue;
    private String message;
}
