package com.technopartner.todo_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequest {
    @NotBlank(message = "El título es requerido")
    private String title;
    
    private String description;
    private LocalDateTime dueDate;
}