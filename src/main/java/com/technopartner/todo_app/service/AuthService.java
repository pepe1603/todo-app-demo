package com.technopartner.todo_app.service;

import com.technopartner.todo_app.dto.*;
import com.technopartner.todo_app.entity.User;
import com.technopartner.todo_app.exception.ApiException;
import com.technopartner.todo_app.repository.UserRepository;
import com.technopartner.todo_app.security.JwtUtils;
import com.technopartner.todo_app.service.otp.OtpEmailService;
import com.technopartner.todo_app.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final OtpEmailService otpEmailService;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String RESET_TOKEN_PREFIX = "reset:";
    private static final String RESET_ATTEMPTS_PREFIX = "reset:attempts:";
    private static final String RESET_RATE_PREFIX = "reset:rate:";
    private static final int RESET_TOKEN_LENGTH = 8;
    private static final int RESET_TOKEN_EXPIRATION_MINUTES = 15;
    private static final int RESET_MAX_ATTEMPTS = 3;
    
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
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
    
    public PasswordResetResponse requestPasswordReset(String email) {
        boolean emailExists = userRepository.findByEmail(email).isPresent();
        
        if (!emailExists) {
            log.warn("Solicitud de recuperación para email no registrado: {}", email);
            return new PasswordResetResponse(
                    "Si el email está registrado, recibirás un código de recuperación",
                    true
            );
        }
        
        User user = userRepository.findByEmail(email).get();
        
        if (!user.isVerified()) {
            return new PasswordResetResponse(
                    "Si el email está registrado y verificado, recibirás un código de recuperación",
                    true
            );
        }
        
        String rateKey = RESET_RATE_PREFIX + email;
        if (redisTemplate.hasKey(rateKey)) {
            Long ttl = redisTemplate.getExpire(rateKey, TimeUnit.MINUTES);
            return new PasswordResetResponse(
                    "Ya solicitaste un código de recuperación. Espera " + ttl + " minutos e intenta de nuevo.",
                    false
            );
        }
        
        String resetToken = generateResetToken();
        String tokenKey = RESET_TOKEN_PREFIX + email;
        
        redisTemplate.opsForValue().set(tokenKey, resetToken, RESET_TOKEN_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        
        redisTemplate.opsForValue().set(rateKey, "1", RESET_TOKEN_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        
        otpEmailService.sendPasswordResetEmail(email, resetToken);
        
        return new PasswordResetResponse(
                "Si el email está registrado y verificado, recibirás un código de recuperación",
                true
        );
    }
    
    public PasswordResetResponse verifyResetCode(String code) {
        String email = findEmailByResetToken(code);
        if (email == null) {
            String attemptsKey = RESET_ATTEMPTS_PREFIX + "global";
            incrementResetAttempts("unknown", attemptsKey);
            return new PasswordResetResponse("Código inválido o expirado", false);
        }
        
        String attemptsKey = RESET_ATTEMPTS_PREFIX + email;
        int attempts = getResetAttempts(email, attemptsKey);
        if (attempts >= RESET_MAX_ATTEMPTS) {
            return new PasswordResetResponse("Has excedido los intentos máximos. Solicita un nuevo código.", false);
        }
        
        String tokenKey = RESET_TOKEN_PREFIX + email;
        String storedToken = redisTemplate.opsForValue().get(tokenKey);
        
        if (storedToken == null || !storedToken.equals(code)) {
            incrementResetAttempts(email, attemptsKey);
            int remaining = RESET_MAX_ATTEMPTS - getResetAttempts(email, attemptsKey);
            return new PasswordResetResponse("Código inválido. Intentos restantes: " + remaining, false);
        }
        
        redisTemplate.delete(tokenKey);
        
        return new PasswordResetResponse("Código verificado correctamente", true);
    }
    
    public PasswordResetResponse resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElse(null);
        
        if (user == null) {
            return new PasswordResetResponse("Usuario no encontrado", false);
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        String attemptsKey = RESET_ATTEMPTS_PREFIX + email;
        redisTemplate.delete(attemptsKey);
        
        log.info("Contraseña reestablecida para {}", email);
        
        return new PasswordResetResponse("Contraseña actualizada correctamente", true);
    }
    
    private String findEmailByResetToken(String token) {
        var keys = redisTemplate.keys(RESET_TOKEN_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        for (var key : keys) {
            String storedToken = redisTemplate.opsForValue().get(key);
            if (token.equals(storedToken)) {
                return key.toString().replace(RESET_TOKEN_PREFIX, "");
            }
        }
        return null;
    }
    
    private String generateResetToken() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < RESET_TOKEN_LENGTH; i++) {
            sb.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    private int getResetAttempts(String email, String attemptsKey) {
        String attempts = redisTemplate.opsForValue().get(attemptsKey);
        return attempts != null ? Integer.parseInt(attempts) : 0;
    }
    
    private void incrementResetAttempts(String email, String attemptsKey) {
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptsKey, RESET_TOKEN_EXPIRATION_MINUTES * 2, TimeUnit.MINUTES);
        }
    }

    public PasswordResetResponse verifyResetToken(String token) {
        String email = findEmailByResetToken(token);
        if (email == null) {
            return new PasswordResetResponse("Token inválido o expirado", false);
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return new PasswordResetResponse("Token inválido o expirado", false);
        }

        String tokenKey = RESET_TOKEN_PREFIX + email;
        String storedToken = redisTemplate.opsForValue().get(tokenKey);

        if (storedToken == null || !storedToken.equals(token)) {
            return new PasswordResetResponse("Token inválido o expirado", false);
        }

        return new PasswordResetResponse("Token válido", true);
    }
}