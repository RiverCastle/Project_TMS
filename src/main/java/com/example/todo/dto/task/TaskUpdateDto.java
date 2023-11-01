package com.example.todo.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskUpdateDto {
    private String newName;
    private String newDesc;
    private LocalDate newStartDate;
    private LocalDate newDueDate;
    private String newWorker;
    private String newStatus;

}
