package com.epms.payroll.Dto.Response;

import com.epms.payroll.Entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String accessToken;
    private String username;
    private String refreshToken;
    private Set<Role> roles;
}