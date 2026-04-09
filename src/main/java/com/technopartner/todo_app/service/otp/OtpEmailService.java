package com.technopartner.todo_app.service.otp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpEmailService {
    
    private final JavaMailSender mailSender;
    
    @Async
    public void sendOtpEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Código de verificación - TechnoPartner");
            message.setText(String.format(
                "Tu código de verificación es: %s\n\n" +
                "Este código expirará en 5 minutos.\n\n" +
                "Si no solicitaste este código, por favor ignora este mensaje.",
                otp
            ));
            
            mailSender.send(message);
            log.info("Email con OTP enviado a {}", email);
        } catch (Exception e) {
            log.error("Error al enviar email con OTP a {}: {}", email, e.getMessage());
        }
    }
}