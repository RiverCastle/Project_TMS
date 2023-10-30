package com.example.todo.service.task;

import com.example.todo.api.notification.NotificationController;
import com.example.todo.domain.entity.MemberEntity;
import com.example.todo.domain.entity.TeamEntity;
import com.example.todo.domain.entity.TaskApiEntity;
import com.example.todo.domain.entity.UsersSubscriptionEntity;
import com.example.todo.domain.entity.enums.SubscriptionStatus;
import com.example.todo.domain.entity.user.User;
import com.example.todo.domain.repository.MemberRepository;
import com.example.todo.domain.repository.TaskApiRepository;
import com.example.todo.domain.repository.TeamReposiotry;
import com.example.todo.domain.repository.UsersSubscriptionRepository;
import com.example.todo.domain.repository.user.UserRepository;
import com.example.todo.dto.NotificationDto;
import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.task.TaskAndTeamDto;
import com.example.todo.dto.task.TaskApiDto;
import com.example.todo.dto.task.TaskCreateDto;
import com.example.todo.dto.team.TeamOverviewDto;
import com.example.todo.exception.ErrorCode;
import com.example.todo.exception.TodoAppException;
import com.example.todo.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;

import static com.example.todo.service.team.TeamService.FREE_TEAM_PARTICIPANT_NUM;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskApiService {
    private final TaskApiRepository taskApiRepository;
    private final TeamReposiotry teamReposiotry;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final UsersSubscriptionRepository usersSubscriptionRepository;
    private final NotificationService notificationService;

    //조직이 존재하는지 확인하는 메소드
    public TeamEntity getTeamById(Long teamId) {
        //해당 Id를 가진 Entity가 존재하는지?
        Optional<TeamEntity> optionalTeamEntity = teamReposiotry.findById(teamId);
        if (optionalTeamEntity.isEmpty())
            throw new TodoAppException(ErrorCode.NOT_FOUND_TEAM);
        return optionalTeamEntity.get();
    }

    //업무 존재하는지 확인하는 메소드
    public TaskApiEntity getTaskById(Long taskId) {
        Optional<TaskApiEntity> optionalTaskApiEntity = taskApiRepository.findById(taskId);
        if (optionalTaskApiEntity.isPresent())
            return optionalTaskApiEntity.get();
        else throw new TodoAppException(ErrorCode.NOT_FOUND_TASK);
    }

    //멤버인지 확인
    public boolean isMemberOfTeam(Long userId, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity teamEntity = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        if (memberRepository.findByTeamAndUser(teamEntity, user).isEmpty()) return false;
        else return true;
    }

    public void isAvailableFunction(TeamEntity teamEntity) {
        if (teamEntity.getParticipantNumMax() > FREE_TEAM_PARTICIPANT_NUM) {
            MemberEntity managerMember = memberRepository.findMemberEntityByTeamAndAndRole(teamEntity, "Manager");
            UsersSubscriptionEntity usersSubscription = usersSubscriptionRepository.findByUsersAndSubscriptionStatus(managerMember.getUser(), SubscriptionStatus.ACTIVE)
                    .orElseThrow(() -> new TodoAppException(ErrorCode.NOT_AVAILABLE_FUNCTION));
            if (usersSubscription.getSubscription().getMaxMember() < teamEntity.getParticipantNumMax())
                throw new TodoAppException(ErrorCode.NOT_AVAILABLE_FUNCTION);
        }
    }

    //업무 등록
    public ResponseDto createTask(Long userId, Long teamId, TaskCreateDto taskCreateDto) {
        log.info("TaskApiService createTask1");
        //팀 존재 확인
        TeamEntity teamEntity = getTeamById(teamId);

        //기능을 사용할 수 있는지 확인
        isAvailableFunction(teamEntity);

        TaskApiEntity taskApiEntity = new TaskApiEntity();
        //조직이 존재하는지 확인
        // 사용자가 해당 팀의 멤버인지 확인
        if (!isMemberOfTeam(userId, teamId)) throw new TodoAppException(ErrorCode.NOT_MATCH_MEMBERID);

        // 담당자 멤버 존재 확인
        log.info("{}", taskCreateDto.getWorker());
        User worker = userRepository.findByUsername(taskCreateDto.getWorker()).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        Long workerUserId = worker.getId();
        if (!isMemberOfTeam(workerUserId, teamId)) throw new TodoAppException(ErrorCode.NOT_MATCH_MEMBERID);
        MemberEntity workerMember = memberRepository.findByTeamAndUser(teamEntity, worker).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));

        taskApiEntity.setUserId(userId);
        taskApiEntity.setTeam(teamEntity);
        taskApiEntity.setTaskName(taskCreateDto.getTaskName());
        taskApiEntity.setTaskDesc(taskCreateDto.getTaskDesc());
        taskApiEntity.setStartDate(taskCreateDto.getStartDate());
        taskApiEntity.setDueDate(taskCreateDto.getDueDate());
        taskApiEntity.setMember(workerMember);
        //현재 날짜 추가
        LocalDate currentDate = LocalDate.now();

        taskApiEntity.setStatus("진행중");
        //현재날짜가 아직 startDate 이전이면 진행예정
        if (taskCreateDto.getStartDate().isAfter(currentDate)) {
            taskApiEntity.setStatus("진행예정");
        } // 현재날짜가 dueDate를 지났으면 완료
        else if (taskCreateDto.getDueDate().isBefore(currentDate)) {
            taskApiEntity.setStatus("완료");
        }
        taskApiEntity = taskApiRepository.save(taskApiEntity);
        return new ResponseDto("업무가 등록되었습니다.");
    }

    //업무 상세 조회
    public TaskApiDto readTask(Long teamId, Long taskId, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) throw new TodoAppException(ErrorCode.NOT_FOUND_USER);

        //조직이 존재하는지 확인
        getTeamById(teamId);
        //업무가 존재하는지 확인
        Optional<TaskApiEntity> optionalTaskApiEntity = taskApiRepository.findById(taskId);
        if (optionalTaskApiEntity.isPresent() && optionalTaskApiEntity.get().getTeam().getId().equals(teamId))
            return TaskApiDto.fromEntity(optionalTaskApiEntity.get());
        else throw new TodoAppException(ErrorCode.NOT_FOUND_TASK);
    }

    public List<TaskApiDto> readTasksAll(Long userId, Long teamId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) throw new TodoAppException(ErrorCode.NOT_FOUND_USER);
        //조직이 존재하는지 확인
        getTeamById(teamId);
        // 멤버 여부 확인
        if (!isMemberOfTeam(userId, teamId)) throw new TodoAppException(ErrorCode.NOT_MATCH_MEMBERID);
        List<TaskApiEntity> taskApiEntities = taskApiRepository.findAllByTeamId(teamId);
        List<TaskApiDto> taskApiDtoList = new ArrayList<>();
        for (TaskApiEntity taskApiEntity : taskApiEntities) taskApiDtoList.add(TaskApiDto.fromEntity(taskApiEntity));
        return taskApiDtoList;
    }

    // 내 전체 업무 조회
    public Map<String, List<TaskApiDto>> getMyTasks(Long userId) {
        Map<String, List<TaskApiDto>> myTasks = new HashMap<>();

        List<MemberEntity> memberEntities = memberRepository.findAllByUserId(userId);
        for (MemberEntity memberEntity : memberEntities) {
            TeamEntity teamEntity = memberEntity.getTeam();
            List<TaskApiEntity> taskApiEntityList = taskApiRepository.findAllByTeamIdAndMember_UserId(teamEntity.getId(), userId);
            List<TaskApiDto> taskApiDtoList = new ArrayList<>();
            for (TaskApiEntity taskApiEntity : taskApiEntityList)
                if (!taskApiEntity.getStatus().equals("완료")) taskApiDtoList.add(TaskApiDto.fromEntity(taskApiEntity));
            myTasks.put(teamEntity.getName(), taskApiDtoList);
        }
        return myTasks;
    }

    //업무 수정
    public ResponseDto updateTask(Long userId, Long teamId, Long taskId, TaskApiDto taskApiDto) {
        TeamEntity teamEntity = getTeamById(teamId);
        //기능을 사용할 수 있는지 확인
        isAvailableFunction(teamEntity);
        TaskApiEntity taskApiEntity = getTaskById(taskId);
        //대상 업무가 대상 팀의 업무가 맞는지
        if (!teamId.equals(taskApiEntity.getTeam().getId()))
            throw new TodoAppException(ErrorCode.NOT_MATCH_TEAM_AND_TASK);
        // 팀 관리자 or 업무 담당자
        if (!taskApiEntity.getUserId().equals(userId) && !taskApiEntity.getWorkerId().equals(userId))
            throw new TodoAppException(ErrorCode.NOT_MATCH_USERID);
        //맞다면 진행
        // 이전 상태 저장
        String previousStatus = taskApiEntity.getStatus();
        taskApiEntity.setUserId(userId);
        taskApiEntity.setTaskName(taskApiDto.getTaskName());
        taskApiEntity.setTaskDesc(taskApiDto.getTaskDesc());
        taskApiEntity.setStartDate(taskApiDto.getStartDate());
        taskApiEntity.setDueDate(taskApiDto.getDueDate());
        //현재 날짜 추가
        LocalDate currentDate = LocalDate.now();

        taskApiEntity.setStatus("진행중");
        //현재날짜가 아직 startDate 이전이면 진행예정
        if (taskApiDto.getStartDate().isAfter(currentDate)) {
            taskApiEntity.setStatus("진행예정");
        } // 현재날짜가 dueDate를 지났으면 완료
        else if (taskApiDto.getDueDate().isBefore(currentDate)) {
            taskApiEntity.setStatus("완료");
        }
        taskApiRepository.save(taskApiEntity);
        //업무 수정후 업무 상태가 이전 상태와 달라졌을때만 알림보내기
        if (!previousStatus.equals(taskApiEntity.getStatus())) {
            sendTaskStatusNotification(taskApiEntity);
        }
        return new ResponseDto("업무가 수정되었습니다.");
    }

    //업무 삭제
    public ResponseDto deleteTask(Long userId, Long teamId, Long taskId) {
        TeamEntity teamEntity = getTeamById(teamId);
        //기능을 사용할 수 있는지 확인
        isAvailableFunction(teamEntity);
        //업무 존재 확인
        TaskApiEntity taskApiEntity = getTaskById(taskId);
        //대상 업무가 대상 팀의 업무가 맞는지
        if (!teamId.equals(taskApiEntity.getTeam().getId()))
            throw new TodoAppException(ErrorCode.NOT_MATCH_TEAM_AND_TASK);
        // 팀 관리자 or 업무 담당자
        if (!taskApiEntity.getUserId().equals(userId) && !taskApiEntity.getWorkerId().equals(userId))
            throw new TodoAppException(ErrorCode.NOT_MATCH_USERID);

        //맞다면 진행
        taskApiRepository.deleteById(taskApiEntity.getId());
        return new ResponseDto("업무를 삭제했습니다.");
    }

    // 팀내 내 업무 조회
    public List<TaskApiDto> getMyTasksInATeam(Long userId, Long teamId) {
        List<TaskApiDto> taskDtoList = new ArrayList<>();
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));
        List<TaskApiEntity> tasks = taskApiRepository.findAllByMember(member);
        for (TaskApiEntity task : tasks) taskDtoList.add(TaskApiDto.fromEntity(task));
        return taskDtoList;

    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateTaskStatusAuto() {
        LocalDate currentDate = LocalDate.now();
        List<TaskApiEntity> tasks = taskApiRepository.findAll();

        for (TaskApiEntity task : tasks) {
            if ("진행중".equals(task.getStatus())) {
                if (task.getDueDate().isBefore(currentDate)) {
                    task.setStatus("완료");
                    taskApiRepository.save(task);

                    sendTaskStatusNotification(task);
                }
            } else if ("진행예정".equals(task.getStatus())) {
                if (task.getStartDate().isBefore(currentDate)) {
                    task.setStatus("진행중");
                    taskApiRepository.save(task);

                    sendTaskStatusNotification(task);
                }
            }
        }
    }

    private void sendTaskStatusNotification(TaskApiEntity taskApiEntity) {
        LocalDateTime currentTime = LocalDateTime.now(); // 현재 시간
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedTime = currentTime.format(formatter);


        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setContent("'" + taskApiEntity.getTeam().getName() + "'팀의 업무'"
                + taskApiEntity.getTaskName() + "'의 진행상황이 '" + taskApiEntity.getStatus() + "'(으)로 변경되었습니다. "
                + formattedTime);

        // 팀에 속한 모든 멤버를 검색
        List<MemberEntity> teamMembers = memberRepository.findAllByTeamId(taskApiEntity.getTeam().getId());

        // 각 멤버에게 알림을 보냄
        for (MemberEntity member : teamMembers) {
            Long userId = member.getUser().getId();
            notificationService.notify(userId, notificationDto);
        }
    }
}