package com.technopartner.todo_app.repository;

import com.technopartner.todo_app.entity.TaskDetailView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDetailViewRepository extends JpaRepository<TaskDetailView, Long> {
    
    @Query("SELECT t FROM TaskDetailView t WHERE t.userId = :userId")
    List<TaskDetailView> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT t FROM TaskDetailView t WHERE t.userId = :userId AND t.status = :status")
    List<TaskDetailView> findByUserIdAndStatus(@Param("userId") Long userId, com.technopartner.todo_app.enums.TaskStatus status);
    
    @Query("SELECT t FROM TaskDetailView t WHERE t.userId = :userId AND t.isOverdue = true")
    List<TaskDetailView> findOverdueByUserId(@Param("userId") Long userId);
    
    List<TaskDetailView> findAll();
}