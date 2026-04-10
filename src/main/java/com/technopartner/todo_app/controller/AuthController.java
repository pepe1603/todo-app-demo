package com.technopartner.todo_app.controller;

import com.technopartner.todo_app.dto.*;
import com.technopartner.todo_app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verify(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }
    
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@Valid @RequestBody EmailRequest email) {
        authService.resendOtp(email.getEmail());
        return ResponseEntity.ok("Código OTP enviado");
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}