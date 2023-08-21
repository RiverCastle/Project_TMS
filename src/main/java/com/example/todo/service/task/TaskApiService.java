package com.example.todo.service.task;

import com.example.todo.domain.entity.chat.ChatRoom;
import com.example.todo.domain.entity.task.TaskApiEntity;
import com.example.todo.domain.repository.TaskApiRepository;
import com.example.todo.domain.repository.chat.ChatRoomRepository;
import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.TaskApiDto;
import com.example.todo.exception.ErrorCode;
import com.example.todo.exception.TodoAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskApiService {
    private final TaskApiRepository taskApiRepository;
    private final ChatRoomRepository chatRoomRepository;

    //조직이 존재하는지 확인하는 메소드
    public TaskApiEntity getTeamById(Long id) {
        //해당 Id를 가진 Entity가 존재하는지?
        Optional<TaskApiEntity> optionalTaskApiEntity = taskApiRepository.findById(id);
        if (optionalTaskApiEntity.isEmpty())
            throw new TodoAppException(ErrorCode.NOT_FOUND_TEAM);
        return optionalTaskApiEntity.get();
    }

    //업무 등록
    public ResponseDto createTask(Long teamId, TaskApiDto taskApiDto) {

        TaskApiEntity taskApiEntity = new TaskApiEntity();
        //조직이 존재하는지 확인
        getTeamById(teamId);

        taskApiEntity.setTeamId(teamId);
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

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setTaskApiEntity(taskApiEntity);
        chatRoomRepository.save(chatRoom);
        return new ResponseDto("업무가 등록되었습니다.");
    }

    //업무 상세 조회
    public TaskApiDto readTask(Long teamId, Long taskId) {
        //조직이 존재하는지 확인
        getTeamById(teamId);
        //업무가 존재하는지 확인
        Optional<TaskApiEntity> optionalTaskApiEntity = taskApiRepository.findById(taskId);
        if (optionalTaskApiEntity.isPresent())
            return TaskApiDto.fromEntity(optionalTaskApiEntity.get());
        else throw new TodoAppException(ErrorCode.NOT_FOUND_TASK);
    }

    public Page<TaskApiDto> readTasksAll(Long teamId, Integer page, Integer limit) {
        //조직이 존재하는지 확인
        getTeamById(teamId);
        Pageable pageable = PageRequest.of(page, limit);
        Page<TaskApiEntity> taskApiEntities = taskApiRepository.findAll(pageable);
        Page<TaskApiDto> taskApiDtos = taskApiEntities.map(TaskApiDto::fromEntity);
        return taskApiDtos;
    }


    //업무 수정
    public ResponseDto updateTask(Long teamId, Long taskId, TaskApiDto taskApiDto) {
        TaskApiEntity taskApiEntity = getTeamById(taskId);
        //대상 업무가 대상 팀의 업무가 맞는지
        if (!teamId.equals(taskApiEntity.getTeamId()))
            throw new TodoAppException(ErrorCode.NOT_MATCH_TEAM_AND_TASK);
        //맞다면 진행
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
        return new ResponseDto("업무가 수정되었습니다.");
    }

    //업무 삭제
    public ResponseDto deleteTask(Long teamId, Long taskId, TaskApiDto taskApiDto) {
        TaskApiEntity taskApiEntity = getTeamById(taskId);
        //대상 업무가 대상 팀의 업무가 맞는지
        if (!teamId.equals(taskApiEntity.getTeamId()))
            throw new TodoAppException(ErrorCode.NOT_MATCH_TEAM_AND_TASK);
        //맞다면 진행
        taskApiRepository.deleteById(taskApiEntity.getId());
        return new ResponseDto("업무를 삭제했습니다.");
    }
}