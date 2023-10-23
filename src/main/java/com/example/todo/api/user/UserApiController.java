package com.example.todo.api.user;

import com.example.todo.domain.Response;
import com.example.todo.domain.entity.UsersSubscriptionEntity;
import com.example.todo.domain.entity.user.User;
import com.example.todo.dto.task.TaskApiDto;
import com.example.todo.dto.team.TeamOverviewDto;
import com.example.todo.dto.user.request.UserJoinRequestDto;
import com.example.todo.dto.user.request.UserUpdateRequestDto;
import com.example.todo.dto.user.response.UserAllResponseDto;
import com.example.todo.dto.user.response.UserJoinResponseDto;
import com.example.todo.dto.user.response.UserUpdateResponseDto;
import com.example.todo.service.read.UserReadService;
import com.example.todo.service.task.TaskApiService;
import com.example.todo.service.user.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserApiController {

    private final UserService userService;
    private final UserReadService readService;
    private final TaskApiService taskApiService;

    @PostMapping
    public Response<UserJoinResponseDto> createUser(@RequestBody final UserJoinRequestDto joinDto) {
        return Response.success(userService.createUser(joinDto));
    }

//    @PostMapping("/join2")
//    public void ct() {
//        userService.createUserData();
//    }
//
//    @PostMapping("/findAll")
//    public void findAllUser() {
//        userService.userSubscription();
//    }

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
        return taskApiService.getMyTasks(userId);
    }

    @GetMapping("/val")
    public String val(@RequestParam("token") String jwt) {
        System.out.println("로그인 완료");
        return jwt;
    }
}
