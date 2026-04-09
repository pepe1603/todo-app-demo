package com.technopartner.todo_app.service.otp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${app.otp.length:6}")
    private int otpLength;
    
    @Value("${app.otp.expiration-minutes:5}")
    private int expirationMinutes;
    
    @Value("${app.otp.max-attempts:3}")
    private int maxAttempts;
    
    private static final String OTP_KEY_PREFIX = "otp:";
    private static final String ATTEMPTS_KEY_PREFIX = "otp:attempts:";
    private static final SecureRandom random = new SecureRandom();
    
    public String generateOtp(String email) {
        String otp = generateRandomOtp();
        String otpKey = OTP_KEY_PREFIX + email;
        
        redisTemplate.opsForValue().set(otpKey, otp, expirationMinutes, TimeUnit.MINUTES);
        
        log.info("OTP generado para {} - expira en {} minutos", email, expirationMinutes);
        
        return otp;
    }
    
    public boolean validateOtp(String email, String otp) {
        String otpKey = OTP_KEY_PREFIX + email;
        String attemptsKey = ATTEMPTS_KEY_PREFIX + email;
        
        String storedOtp = redisTemplate.opsForValue().get(otpKey);
        
        if (storedOtp == null) {
            log.warn("OTP expirado o no existe para {}", email);
            return false;
        }
        
        if (storedOtp.equals(otp)) {
            redisTemplate.delete(otpKey);
            redisTemplate.delete(attemptsKey);
            log.info("OTP válido para {}", email);
            return true;
        }
        
        incrementAttempts(email, attemptsKey);
        log.warn("OTP inválido para {}, intentos: {}", email, getAttempts(email, attemptsKey));
        
        return false;
    }
    
    public boolean isOtpExpired(String email) {
        String otpKey = OTP_KEY_PREFIX + email;
        return redisTemplate.opsForValue().get(otpKey) == null;
    }
    
    public int getRemainingAttempts(String email) {
        String attemptsKey = ATTEMPTS_KEY_PREFIX + email;
        int attempts = getAttempts(email, attemptsKey);
        return Math.max(0, maxAttempts - attempts);
    }
    
    public boolean canRequestOtp(String email) {
        String otpKey = OTP_KEY_PREFIX + email;
        return redisTemplate.opsForValue().get(otpKey) == null;
    }
    
    private void incrementAttempts(String email, String attemptsKey) {
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptsKey, expirationMinutes * 2, TimeUnit.MINUTES);
        }
    }
    
    private int getAttempts(String email, String attemptsKey) {
        String attempts = redisTemplate.opsForValue().get(attemptsKey);
        return attempts != null ? Integer.parseInt(attempts) : 0;
    }
    
    private String generateRandomOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}