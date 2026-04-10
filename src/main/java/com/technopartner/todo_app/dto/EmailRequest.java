package com.technopartner.todo_app.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailRequest {
    @Email
    private String email;
}