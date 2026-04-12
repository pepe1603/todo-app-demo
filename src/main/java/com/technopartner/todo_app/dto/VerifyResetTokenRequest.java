package com.technopartner.todo_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyResetTokenRequest {
    @NotBlank(message = "El token es requerido")
    private String token;
}