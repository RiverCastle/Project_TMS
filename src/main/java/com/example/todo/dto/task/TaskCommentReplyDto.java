package com.example.todo.dto.task;


import com.example.todo.domain.entity.TaskCommentReplyEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskCommentReplyDto {
    private String  writerName;
    @NotNull(message = "내용을 작성해주세요.")
    private String reply;
    private LocalDateTime createdAt;
    public static TaskCommentReplyDto fromEntity(TaskCommentReplyEntity entity) {
        TaskCommentReplyDto replyReadDto = new TaskCommentReplyDto();
        replyReadDto.setWriterName(entity.getWriter().getUser().getUsername());
        replyReadDto.setReply(entity.getReply());
        replyReadDto.setCreatedAt(entity.getCreatedAt());
        return replyReadDto;
    }
}