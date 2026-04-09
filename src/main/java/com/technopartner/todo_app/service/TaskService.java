package com.technopartner.todo_app.service;

import com.technopartner.todo_app.dto.TaskRequest;
import com.technopartner.todo_app.dto.TaskResponse;
import com.technopartner.todo_app.entity.Task;
import com.technopartner.todo_app.entity.User;
import com.technopartner.todo_app.repository.TaskRepository;
import com.technopartner.todo_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    public TaskResponse createTask(TaskRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setUser(user);
        
        return toResponse(taskRepository.save(task));
    }
    
    public List<TaskResponse> getTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return taskRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public TaskResponse updateTask(Long taskId, TaskRequest request, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        
        if (!task.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("No tienes permiso para modificar esta tarea");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        
        return toResponse(taskRepository.save(task));
    }
    
    public TaskResponse toggleComplete(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        
        if (!task.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("No tienes permiso para modificar esta tarea");
        }
        
        task.setCompleted(!task.isCompleted());
        
        return toResponse(taskRepository.save(task));
    }
    
    public void deleteTask(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        
        if (!task.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("No tienes permiso para eliminar esta tarea");
        }
        
        taskRepository.delete(task);
    }
    
    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setCompleted(task.isCompleted());
        response.setCreatedAt(task.getCreatedAt());
        response.setDueDate(task.getDueDate());
        return response;
    }
}