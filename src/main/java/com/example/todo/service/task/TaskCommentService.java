package com.example.todo.service.task;

import com.example.todo.dto.task.TaskCommentReplyCreateDto;
import com.example.todo.domain.entity.*;
import com.example.todo.domain.entity.user.User;
import com.example.todo.domain.repository.*;
import com.example.todo.domain.repository.user.UserRepository;
import com.example.todo.dto.task.*;
import com.example.todo.exception.ErrorCode;
import com.example.todo.exception.TodoAppException;
import com.example.todo.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskCommentService {
    private final TaskCommentRepository taskCommentRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final TeamReposiotry teamReposiotry;
    private final TaskApiRepository taskApiRepository;
    private final NotificationService notificationService;
    private final TaskCommentReplyRepository taskCommentReplyRepository;

    public void createTaskComment(Long userId, Long teamId, Long taskId, TaskCommentCreateDto taskCommentCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        TaskApiEntity task = taskApiRepository.findById(taskId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TASK));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));

        TaskCommentEntity taskCommentEntity = new TaskCommentEntity();

        taskCommentEntity.setWriter(member);
        taskCommentEntity.setContent(taskCommentCreateDto.getContent());
        taskCommentEntity.setTaskApiEntity(task);
        taskCommentRepository.save(taskCommentEntity);

        // 댓글을 작성한 사용자와 업무 관리자를 비교
        if (!user.getId().equals(task.getWorkerId())) {
            LocalDateTime currentTime = LocalDateTime.now(); // 현재 시간
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedTime = currentTime.format(formatter);

            // 알림 메시지 생성
            String message = "'" + team.getName() + "'팀의 " + user.getUsername() + "님이'" + task.getTaskName() + "'에 메시지를 남겼습니다. createdTime:" + formattedTime;            // 관리자에게 알림을 보냄
            notificationService.notify(task.getWorkerId(), message);
        }
    }

    public Page<TaskCommentReadDto> readTaskCommentsPage(Long userId, Long teamId, Long taskId, Integer page, Integer limit) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        TaskApiEntity task = taskApiRepository.findById(taskId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TASK));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));

        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").ascending());
        Page<TaskCommentEntity> taskCommentEntityPage = taskCommentRepository.findAllByTaskApiEntityAndDeletedAtIsNull(task, pageable);
        Page<TaskCommentReadDto> commentDtoPage = taskCommentEntityPage.map(TaskCommentReadDto::fromEntity);
        return commentDtoPage;
    }

    public void updateTaskComment(Long userId, Long teamId, Long taskId, Long commentId, TaskCommentUpdateDto taskCommentUpdateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        TaskApiEntity task = taskApiRepository.findById(taskId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TASK));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));
        TaskCommentEntity taskComment = taskCommentRepository.findById(commentId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TASK_COMMENT));

        taskComment.setContent(taskCommentUpdateDto.getContent());
        taskCommentRepository.save(taskComment);
    }

    //답글 달기
    public TaskCommentReplyEntity addReply(Long userId, Long teamId, Long taskId, Long commentId, TaskCommentReplyCreateDto taskCommentReplyDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        TaskApiEntity task = taskApiRepository.findById(taskId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TASK));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));
        TaskCommentEntity taskComment = taskCommentRepository.findById(commentId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TASK_COMMENT));

        //맞다면 진행한다.
        TaskCommentReplyEntity replyEntity = new TaskCommentReplyEntity();
        replyEntity.setTaskCommentEntity(taskComment);
        replyEntity.setWriter(member);
        replyEntity.setReply(taskCommentReplyDto.getContent());
        LocalDateTime currentTime = LocalDateTime.now(); // 현재 시간
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedTime = currentTime.format(formatter);

        // 알림을 받을 사용자의 ID를 가져오기 위해 TaskCommentEntity를 사용하여 작성자의 ID를 가져옴
        Long receiveUserId = taskComment.getWriter().getId();
        boolean isWoker = userId.equals(task.getWorkerId());

        // 답글을 작성한 사용자와 댓글 작성자가 다를 때 알림을 보냄
        if (!userId.equals(receiveUserId)) {
            // 알림 메시지 생성
            String message = "'" + team.getName() + "'팀의 " + user.getUsername() + "님이'" + task.getTaskName() + "'에 메시지를 남겼습니다. createdTime:" + formattedTime;
            // 댓글 작성자에게 알림 보내기
            notificationService.notify(receiveUserId, message);
            if (!isWoker) { //업무담당자도 아닌, 제3자라면
                // 업무 담당자에게도 알림 보내기
                notificationService.notify(task.getWorkerId(), message);
            }
        } else {
            // 댓쓴이가 답글을 달았다면, 담당자에게 알림 보내기
            // 알림 메시지 생성
            String message = "'" + team.getName() + "'팀의 " + user.getUsername() + "님이'" + task.getTaskName() + "'에 메시지를 남겼습니다. createdTime:" + formattedTime;
            // 업무 담당자에게 알림 보내기
            notificationService.notify(task.getWorkerId(), message);
        }
        return taskCommentReplyRepository.save(replyEntity);
    }

    public void deleteComment(Long userId, Long teamId, Long taskId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        TaskApiEntity task = taskApiRepository.findById(taskId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TASK));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));
        TaskCommentEntity taskComment = taskCommentRepository.findById(commentId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TASK_COMMENT));
        if (!taskComment.getWriter().equals(member)) throw new TodoAppException(ErrorCode.NOT_WRITER);

        taskComment.setDeletedAt(LocalDateTime.now());
        taskCommentRepository.save(taskComment);
    }
}
