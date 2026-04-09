package com.technopartner.todo_app.repository;

import com.technopartner.todo_app.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByUserIdAndCompleted(Long userId, boolean completed);
}