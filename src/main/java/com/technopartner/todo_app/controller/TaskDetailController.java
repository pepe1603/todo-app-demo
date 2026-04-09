package com.technopartner.todo_app.controller;

import com.technopartner.todo_app.entity.TaskDetailView;
import com.technopartner.todo_app.enums.TaskStatus;
import com.technopartner.todo_app.repository.TaskDetailViewRepository;
import com.technopartner.todo_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/details")
@RequiredArgsConstructor
public class TaskDetailController {
    
    private final TaskDetailViewRepository taskDetailRepository;
    private final UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<List<TaskDetailView>> getAllTaskDetails(Authentication auth) {
        Long userId = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
        
        return ResponseEntity.ok(taskDetailRepository.findByUserId(userId));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDetailView>> getTaskDetailsByStatus(
            @PathVariable TaskStatus status,
            Authentication auth) {
        
        Long userId = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
        
        return ResponseEntity.ok(taskDetailRepository.findByUserIdAndStatus(userId, status));
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDetailView>> getOverdueTasks(Authentication auth) {
        Long userId = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
        
        return ResponseEntity.ok(taskDetailRepository.findOverdueByUserId(userId));
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<TaskDetailView>> getAllTaskDetailsAdmin() {
        return ResponseEntity.ok(taskDetailRepository.findAll());
    }
}