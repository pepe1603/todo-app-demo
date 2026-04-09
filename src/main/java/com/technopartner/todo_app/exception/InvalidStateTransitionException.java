package com.technopartner.todo_app.exception;

import com.technopartner.todo_app.enums.TaskStatus;

public class InvalidStateTransitionException extends ApiException {
    
    private final TaskStatus currentStatus;
    private final TaskStatus targetStatus;
    
    public InvalidStateTransitionException(TaskStatus current, TaskStatus target) {
        super(
            String.format("No se puede transiciónar de '%s' a '%s'", 
                current.getDisplayName(), target.getDisplayName()),
            org.springframework.http.HttpStatus.BAD_REQUEST
        );
        this.currentStatus = current;
        this.targetStatus = target;
    }
    
    public TaskStatus getCurrentStatus() {
        return currentStatus;
    }
    
    public TaskStatus getTargetStatus() {
        return targetStatus;
    }
}