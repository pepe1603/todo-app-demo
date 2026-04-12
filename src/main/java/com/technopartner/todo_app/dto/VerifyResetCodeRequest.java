package com.technopartner.todo_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyResetCodeRequest {
    @NotBlank(message = "El código es requerido")
    private String code;
}