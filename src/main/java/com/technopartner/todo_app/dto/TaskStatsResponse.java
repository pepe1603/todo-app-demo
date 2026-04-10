package com.technopartner.todo_app.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class TaskStatsResponse {
    private Long total;
    private Long pending;
    private Long inProgress;
    private Long completed;
    private Long cancelled;
    private Long overdue;
    private Double completionRate;
}