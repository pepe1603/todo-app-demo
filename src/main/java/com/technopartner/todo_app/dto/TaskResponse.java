package com.technopartner.todo_app.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
}