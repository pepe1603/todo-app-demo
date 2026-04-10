package com.technopartner.todo_app.controller;

import com.technopartner.todo_app.dto.TaskRequest;
import com.technopartner.todo_app.dto.TaskResponse;
import com.technopartner.todo_app.dto.TaskStatsResponse;
import com.technopartner.todo_app.enums.TaskStatus;
import com.technopartner.todo_app.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Validated
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
    
    @GetMapping("/stats")
    public ResponseEntity<TaskStatsResponse> getStats(Authentication auth) {
        return ResponseEntity.ok(taskService.getStats(auth.getName()));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request, Authentication auth) {
        return ResponseEntity.ok(taskService.updateTask(id, request, auth.getName()));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> changeStatus(
            @PathVariable Long id,
            @RequestParam @NotNull TaskStatus status,
            Authentication auth) {
        return ResponseEntity.ok(taskService.changeStatus(id, status, auth.getName()));
    }
    
    @PatchMapping("/{id}/start")
    public ResponseEntity<TaskResponse> startTask(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(taskService.startTask(id, auth.getName()));
    }
    
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(taskService.completeTask(id, auth.getName()));
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TaskResponse> cancelTask(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(taskService.cancelTask(id, auth.getName()));
    }
    
    @PatchMapping("/{id}/reopen")
    public ResponseEntity<TaskResponse> reopenTask(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(taskService.reopenTask(id, auth.getName()));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication auth) {
        taskService.deleteTask(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}