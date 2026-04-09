package com.technopartner.todo_app.service;

import com.technopartner.todo_app.dto.AuthResponse;
import com.technopartner.todo_app.dto.LoginRequest;
import com.technopartner.todo_app.dto.RegisterRequest;
import com.technopartner.todo_app.entity.User;
import com.technopartner.todo_app.repository.UserRepository;
import com.technopartner.todo_app.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        userRepository.save(user);
        
        String token = jwtUtils.generateToken(user.getEmail());
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName());
    }
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String token = jwtUtils.generateToken(user.getEmail());
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName());
    }
}