package com.example.todo.dto.task;


import com.example.todo.domain.entity.TaskCommentReplyEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCommentReplyDto {
    private String  writerName;
    @NotNull(message = "내용을 작성해주세요.")
    private String reply;

    public static TaskCommentReplyDto fromEntity(TaskCommentReplyEntity entity) {
        TaskCommentReplyDto replyReadDto = new TaskCommentReplyDto();
        replyReadDto.setWriterName(entity.getWriter().getUser().getUsername());
        replyReadDto.setReply(entity.getReply());

        return replyReadDto;
    }
}