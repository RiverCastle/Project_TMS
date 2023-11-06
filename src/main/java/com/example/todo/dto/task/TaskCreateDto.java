package com.example.todo.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskCreateDto {
    @NotNull(message = "제목을 작성해주세요.")
    private String taskName;
    @NotNull(message = "설명을 작성해주세요.")
    private String taskDesc;
    @NotNull(message = "시작일을 입력해주세요")
    private LocalDate startDate;
    @NotNull(message = "마감일을 작성해주세요.")
    private LocalDate dueDate;
    private String worker;
}
