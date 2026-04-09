package com.technopartner.todo_app.service;

import com.technopartner.todo_app.dto.*;
import com.technopartner.todo_app.entity.User;
import com.technopartner.todo_app.exception.ApiException;
import com.technopartner.todo_app.repository.UserRepository;
import com.technopartner.todo_app.security.JwtUtils;
import com.technopartner.todo_app.service.otp.OtpEmailService;
import com.technopartner.todo_app.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final OtpEmailService otpEmailService;
    
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw ApiException.badRequest("El email ya está registrado");
        }
        
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);
        
        userRepository.save(user);
        
        String otp = otpService.generateOtp(user.getEmail());
        otpEmailService.sendOtpEmail(user.getEmail(), otp);
        
        return new RegisterResponse(
                "Usuario creado. Por favor verifica tu cuenta con el código enviado a tu email.",
                user.getId(),
                user.getEmail()
        );
    }
    
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado"));
        
        if (user.isVerified()) {
            throw ApiException.badRequest("La cuenta ya está verificada");
        }
        
        if (!otpService.validateOtp(request.getEmail(), request.getOtp())) {
            int remaining = otpService.getRemainingAttempts(request.getEmail());
            throw ApiException.badRequest("Código inválido. Intentos restantes: " + remaining);
        }
        
        user.setVerified(true);
        user.setVerifiedAt(LocalDateTime.now());
        userRepository.save(user);
        
        String token = jwtUtils.generateToken(user.getEmail());
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName());
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        
        if (!user.isVerified()) {
            throw ApiException.unauthorized("Por favor verifica tu cuenta primero. Solicita un nuevo código OTP.");
        }
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        String token = jwtUtils.generateToken(user.getEmail());
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName());
    }
    
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado"));
        
        if (user.isVerified()) {
            throw ApiException.badRequest("La cuenta ya está verificada");
        }
        
        if (!otpService.canRequestOtp(email)) {
            throw ApiException.badRequest("Ya solicitaste un código. Espera un momento e intenta de nuevo.");
        }
        
        String otp = otpService.generateOtp(email);
        otpEmailService.sendOtpEmail(email, otp);
    }
}