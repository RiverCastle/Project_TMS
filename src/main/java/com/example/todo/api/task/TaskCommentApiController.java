package com.example.todo.api.task;

import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.task.TaskCommentReplyCreateDto;
import com.example.todo.dto.task.TaskCommentCreateDto;
import com.example.todo.dto.task.TaskCommentReadDto;
import com.example.todo.dto.task.TaskCommentReplyDto;
import com.example.todo.dto.task.TaskCommentUpdateDto;
import com.example.todo.service.notification.NotificationService;
import com.example.todo.service.task.TaskCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team/{teamId}/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class TaskCommentApiController {
    private final TaskCommentService taskCommentService;

    @PostMapping
    public ResponseDto createTaskComment(Authentication authentication,
                                         @PathVariable("teamId") Long teamId,
                                         @PathVariable("taskId") Long taskId,
                                         @RequestBody TaskCommentCreateDto taskCommentCreateDto) {
        Long userId = Long.parseLong(authentication.getName());

        taskCommentService.createTaskComment(userId, teamId, taskId, taskCommentCreateDto);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Task에 댓글이 등록되었습니다.");
        return responseDto;
    }

    @GetMapping
    public Page<TaskCommentReadDto> readTaskCommentReadDtoPage(Authentication authentication,
                                                               @PathVariable("teamId") Long teamId,
                                                               @PathVariable("taskId") Long taskId,
                                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                               @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Long userId = Long.parseLong(authentication.getName());
        Page<TaskCommentReadDto> taskCommentReadDtoPage = taskCommentService.readTaskCommentsPage(userId, teamId, taskId, page, limit);
        return taskCommentReadDtoPage;
    }

    @PutMapping("/{commentId}")
    public ResponseDto updateTaskComment(Authentication authentication,
                                         @PathVariable("teamId") Long teamId,
                                         @PathVariable("taskId") Long taskId,
                                         @PathVariable("commentId") Long commentId,
                                         TaskCommentUpdateDto taskCommentUpdateDto) {
        Long userId = Long.parseLong(authentication.getName());
        taskCommentService.updateTaskComment(userId, teamId, taskId, commentId, taskCommentUpdateDto);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Task에 댓글이 수정되었습니다.");
        return responseDto;
    }
    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseDto deleteComment(Authentication authentication,
                                     @PathVariable("teamId") Long teamId,
                                     @PathVariable("taskId") Long taskId,
                                     @PathVariable("commentId") Long commentId) {
        Long userId = Long.parseLong(authentication.getName());
        taskCommentService.deleteComment(userId, teamId, taskId, commentId);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("댓글이 성공적으로 삭제되었습니다.");
        return responseDto;
    }

    //답글 달기
    @PostMapping("/{commentId}/reply")
    public ResponseDto addReply(
            Authentication authentication,
            @PathVariable("teamId") Long teamId,
            @PathVariable("taskId") Long taskId,
            @PathVariable("commentId") Long commentId,
            @RequestBody TaskCommentReplyCreateDto taskCommentReplyCreateDtoDto) {
        Long userId = Long.parseLong(authentication.getName());
        taskCommentService.addReply(userId, teamId, taskId, commentId, taskCommentReplyCreateDtoDto);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("댓글에 답글을 남겼습니다.");
        return responseDto;
    }

    @DeleteMapping("/{commentId}/reply/{replyId}")
    public ResponseDto deleteReply(Authentication authentication,
                                   @PathVariable("teamId") Long teamId,
                                   @PathVariable("taskId") Long taskId,
                                   @PathVariable("commentId") Long commentId,
                                   @PathVariable("replyId") Long replyId) {
        Long userId = Long.parseLong(authentication.getName());
        taskCommentService.deleteReply(userId, teamId, taskId, commentId, replyId);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("성공적으로 답글이 삭제되었습니다.");
        return responseDto;
    }

}
