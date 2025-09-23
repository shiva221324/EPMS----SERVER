package com.epms.payroll.Services;

import com.epms.payroll.Dto.Request.LoginRequestDTO;
import com.epms.payroll.Dto.Response.LoginResponseDTO;
import com.epms.payroll.Entities.User;
import com.epms.payroll.Exception.custom.ResourceNotFoundException;
import com.epms.payroll.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user=userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(()->new ResourceNotFoundException("User not Found"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()
                )
        );
        String token =jwtService.generateToken(user);
        return LoginResponseDTO.builder()
                .accessToken(token)
                .username(user.getUsername())
                .roles(user.getRoles())
                .build();
    }
    public void logout(String token) {
        jwtService.blacklistToken(token);
    }
}
