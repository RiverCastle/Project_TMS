package com.example.todo.service.user;

import com.example.todo.domain.entity.SubscriptionEntity;
import com.example.todo.domain.entity.UsersSubscriptionEntity;
import com.example.todo.domain.entity.enums.Role;
import com.example.todo.domain.entity.enums.SubscriptionStatus;
import com.example.todo.domain.entity.user.User;
import com.example.todo.domain.repository.SubscriptionRepository;
import com.example.todo.domain.repository.UsersSubscriptionRepository;
import com.example.todo.domain.repository.user.UserRepository;
import com.example.todo.dto.user.request.UserJoinRequestDto;
import com.example.todo.dto.user.request.UserLoginRequestDto;
import com.example.todo.dto.user.request.UserUpdateRequestDto;
import com.example.todo.dto.user.response.UserJoinResponseDto;
import com.example.todo.dto.user.response.UserUpdateResponseDto;
import com.example.todo.exception.ErrorCode;
import com.example.todo.exception.TodoAppException;
import com.example.todo.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static com.example.todo.exception.ErrorCode.ALREADY_USER_USERNAME;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final SubscriptionRepository subscriptionRepository;
    private final UsersSubscriptionRepository usersSubscriptionRepository;

    @Transactional
    public UserJoinResponseDto createUser(UserJoinRequestDto joinDto) {
        passwordCheck(joinDto);
        validateDuplicateUsername(joinDto.getUsername());
        User user = joinDto.toEntity(passwordEncoder.encode(joinDto.getPassword()));
        return new UserJoinResponseDto(userRepository.save(user));
    }

    @Transactional
    public UserUpdateResponseDto updateUser(final UserUpdateRequestDto updateDto, final Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.updateProfile(updateDto, passwordEncoder.encode(updateDto.getPassword()), "image");
        return new UserUpdateResponseDto(user);
    }


    @Transactional
    public void createAdminUser(){
        if (!userRepository.existsByUsername("admin")){
            User user = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("password"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(user);
        }
    }

    private void validateDuplicateUsername(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new TodoAppException(ALREADY_USER_USERNAME, ALREADY_USER_USERNAME.getMessage());
                });
    }
    private void passwordCheck(UserJoinRequestDto userJoinRequestDto) {
        if (!userJoinRequestDto.getPassword().equals(userJoinRequestDto.getPasswordCheck())) throw new TodoAppException(ErrorCode.PASSWORD_PASSWORDCHECK_MISMATCH);
    }

    public String login(UserLoginRequestDto loginRequestDto) {
        User user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        if (passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            return tokenProvider.createAccessToken(user);
        } else throw new TodoAppException(ErrorCode.LOGIN_FAILS);
    }
}
