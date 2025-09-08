package com.epms.payroll.Dto.Request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
@Data
public class LoginRequestDTO {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
