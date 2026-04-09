package com.technopartner.todo_app.enums;

import java.util.Set;

public enum TaskStatusTransition {
    PENDING(Set.of(TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED)),
    IN_PROGRESS(Set.of(TaskStatus.COMPLETED, TaskStatus.PENDING, TaskStatus.CANCELLED)),
    COMPLETED(Set.of()), 
    CANCELLED(Set.of(TaskStatus.PENDING));

    private final Set<TaskStatus> allowedTransitions;

    TaskStatusTransition(Set<TaskStatus> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public boolean canTransitionTo(TaskStatus target) {
        return allowedTransitions.contains(target);
    }
}