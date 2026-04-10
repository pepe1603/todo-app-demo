package com.technopartner.todo_app.service;

import com.technopartner.todo_app.dto.TaskRequest;
import com.technopartner.todo_app.dto.TaskResponse;
import com.technopartner.todo_app.entity.Task;
import com.technopartner.todo_app.entity.User;
import com.technopartner.todo_app.enums.TaskStatus;
import com.technopartner.todo_app.enums.TaskStatusTransition;
import com.technopartner.todo_app.exception.ApiException;
import com.technopartner.todo_app.exception.InvalidStateTransitionException;
import com.technopartner.todo_app.repository.TaskRepository;
import com.technopartner.todo_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    // Sin @CacheEvict para evitar errores de serialización
    public TaskResponse createTask(TaskRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado"));
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.PENDING);
        task.setUser(user);
        
        return toResponse(taskRepository.save(task));
    }
    
    // Sin caché para evitar errores de serialización con Redis
    public List<TaskResponse> getTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado"));
        
        return taskRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public TaskResponse updateTask(Long taskId, TaskRequest request, String userEmail) {
        Task task = getTaskForUser(taskId, userEmail);
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        
        return toResponse(taskRepository.save(task));
    }
    
    public TaskResponse changeStatus(Long taskId, TaskStatus newStatus, String userEmail) {
        Task task = getTaskForUser(taskId, userEmail);
        
        TaskStatus currentStatus = task.getStatus();
        TaskStatusTransition currentTransition = TaskStatusTransition.valueOf(currentStatus.name());
        
        if (!currentTransition.canTransitionTo(newStatus)) {
            throw new InvalidStateTransitionException(currentStatus, newStatus);
        }
        
        task.setStatus(newStatus);
        
        if (newStatus == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }
        
        return toResponse(taskRepository.save(task));
    }
    
    public TaskResponse startTask(Long taskId, String userEmail) {
        return changeStatus(taskId, TaskStatus.IN_PROGRESS, userEmail);
    }
    
    public TaskResponse completeTask(Long taskId, String userEmail) {
        return changeStatus(taskId, TaskStatus.COMPLETED, userEmail);
    }
    
    public TaskResponse cancelTask(Long taskId, String userEmail) {
        return changeStatus(taskId, TaskStatus.CANCELLED, userEmail);
    }
    
    public TaskResponse reopenTask(Long taskId, String userEmail) {
        return changeStatus(taskId, TaskStatus.PENDING, userEmail);
    }
    
    public void deleteTask(Long taskId, String userEmail) {
        Task task = getTaskForUser(taskId, userEmail);
        taskRepository.delete(task);
    }
    
    private Task getTaskForUser(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> ApiException.notFound("Tarea no encontrada"));
        
        if (!task.getUser().getEmail().equals(userEmail)) {
            throw ApiException.unauthorized("No tienes permiso para modificar esta tarea");
        }
        
        return task;
    }
    
    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus().name());
        response.setCompletedAt(task.getCompletedAt());
        response.setCreatedAt(task.getCreatedAt());
        response.setDueDate(task.getDueDate());
        return response;
    }
}