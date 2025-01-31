package com.example.todo.api.user;

import com.example.todo.config.filter.JwtTokenDto;
import com.example.todo.domain.Response;
import com.example.todo.domain.entity.user.User;
import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.task.TaskApiDto;
import com.example.todo.dto.team.TeamOverviewDto;
import com.example.todo.dto.user.request.UserJoinRequestDto;
import com.example.todo.dto.user.request.UserLoginRequestDto;
import com.example.todo.dto.user.request.UserUpdateRequestDto;
import com.example.todo.dto.user.response.UserAllResponseDto;
import com.example.todo.dto.user.response.UserJoinResponseDto;
import com.example.todo.dto.user.response.UserUpdateResponseDto;

import com.example.todo.service.read.UserReadService;
import com.example.todo.service.task.TaskApiService;
import com.example.todo.service.user.UserService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserApiController {

    private final UserService userService;
    private final UserReadService readService;
    private final TaskApiService taskApiService;

    @PostMapping
    public ResponseDto createUser(@RequestBody UserJoinRequestDto userJoinRequestDto) {
        userService.createUser(userJoinRequestDto);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("회원가입이 완료되었습니다.");
        return responseDto;
    }

    @GetMapping("/find")
    public List<UserAllResponseDto> findAll() {
        return readService.getUsersWithExpirationOneWeek();
    }

    @PutMapping
    public Response<UserUpdateResponseDto> updateUser(@RequestBody final UserUpdateRequestDto updateDto,
                                                      final Authentication authentication) {
        final Long userId = Long.parseLong(authentication.getName());
        return Response.success(userService.updateUser(updateDto, userId));
    }

    //내 업무 모아보기
    @GetMapping("/myTasks")
    public Map<TeamOverviewDto, List<TaskApiDto>> getMyTasks(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        log.info("내 업무 조회 api");
        return taskApiService.getMyTasks(userId);
    }
}
