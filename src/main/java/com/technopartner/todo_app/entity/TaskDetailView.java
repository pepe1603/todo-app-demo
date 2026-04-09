package com.technopartner.todo_app.entity;

import com.technopartner.todo_app.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "v_task_details")
@Data
public class TaskDetailView {
    
    @Id
    private Long id;
    
    private String title;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "due_date")
    private java.time.LocalDateTime dueDate;
    
    @Column(name = "completed_at")
    private java.time.LocalDateTime completedAt;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "user_email")
    private String userEmail;
    
    @Column(name = "user_full_name")
    private String userFullName;
    
    @Column(name = "days_remaining")
    private Integer daysRemaining;
    
    @Column(name = "is_overdue")
    private Boolean isOverdue;
    
    @Column(name = "days_overdue")
    private Integer daysOverdue;
}