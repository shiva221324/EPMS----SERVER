package com.epms.payroll.Controller;


import com.epms.payroll.Dto.RegisterRequestDTO;
import com.epms.payroll.Dto.Request.LoginRequestDTO;
import com.epms.payroll.Dto.Response.LoginResponseDTO;
import com.epms.payroll.Entities.User;
import com.epms.payroll.Services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO loginResponseDTO=authenticationService.login(loginRequestDTO);
        return ResponseEntity
                .ok()
                .header("Authorization", "Bearer " + loginResponseDTO.getAccessToken())
                .body(loginResponseDTO);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // remove "Bearer "
        authenticationService.logout(jwt);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequestDTO user) {
        User createdUser = authenticationService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }



}