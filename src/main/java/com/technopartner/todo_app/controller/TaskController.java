package com.technopartner.todo_app.controller;

import com.technopartner.todo_app.dto.TaskRequest;
import com.technopartner.todo_app.dto.TaskResponse;
import com.technopartner.todo_app.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request, Authentication auth) {
        return ResponseEntity.ok(taskService.createTask(request, auth.getName()));
    }
    
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(Authentication auth) {
        return ResponseEntity.ok(taskService.getTasks(auth.getName()));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request, Authentication auth) {
        return ResponseEntity.ok(taskService.updateTask(id, request, auth.getName()));
    }
    
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TaskResponse> toggleComplete(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(taskService.toggleComplete(id, auth.getName()));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication auth) {
        taskService.deleteTask(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}