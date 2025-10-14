package com.epms.payroll.Services;

import com.epms.payroll.Dto.RegisterRequestDTO;
import com.epms.payroll.Dto.Request.LoginRequestDTO;
import com.epms.payroll.Dto.Response.LoginResponseDTO;
import com.epms.payroll.Entities.Role;
import com.epms.payroll.Entities.User;
import com.epms.payroll.Exception.codes.ErrorCode;
import com.epms.payroll.Exception.custom.BusinessException;
import com.epms.payroll.Exception.custom.ResourceNotFoundException;
import com.epms.payroll.Repositories.RoleRepository;
import com.epms.payroll.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import com.epms.payroll.Exception.custom.BusinessException;
import com.epms.payroll.Exception.codes.ErrorCode;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoleRepository roleRepository;


    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + loginRequestDTO.getUsername()));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new BusinessException(
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }
        String token = jwtService.generateToken(user);
        return LoginResponseDTO.builder()
                .accessToken(token)
                .username(user.getUsername())
                .roles(user.getRoles())
                .build();
    }

    public void logout(String token) {
        jwtService.blacklistToken(token);
    }


    public User createUser(RegisterRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Set<Role> roles = dto.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.RESOURCE_NOT_FOUND, "Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        return userRepository.save(user);
    }

}
