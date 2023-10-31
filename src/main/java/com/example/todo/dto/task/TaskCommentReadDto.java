package com.example.todo.dto.task;

import com.example.todo.domain.entity.TaskCommentEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class TaskCommentReadDto {
    private String writerName;
    private String content;
    private  List<TaskCommentReplyDto> replies;


    public static TaskCommentReadDto fromEntity(TaskCommentEntity entity) {
        TaskCommentReadDto taskCommentReadDto = new TaskCommentReadDto();
        taskCommentReadDto.setWriterName(entity.getWriter().getUser().getUsername());
        taskCommentReadDto.setContent(entity.getContent());

        List<TaskCommentReplyDto> replyDtos = entity.getReplies().stream()
                .map(replyEntity -> TaskCommentReplyDto.fromEntity(replyEntity))
                .collect(Collectors.toList());

        taskCommentReadDto.setReplies(replyDtos);

        return taskCommentReadDto;
    }
}
