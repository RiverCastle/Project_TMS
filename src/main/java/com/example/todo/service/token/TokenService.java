package com.example.todo.service.token;

import com.example.todo.domain.entity.user.User;
import com.example.todo.domain.repository.user.UserRepository;
import com.example.todo.dto.user.request.UserLoginRequestDto;
import com.example.todo.exception.ErrorCode;
import com.example.todo.exception.TodoAppException;
import com.example.todo.jwt.RefreshTokenService;
import com.example.todo.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    // 로그인 시 Access Token, Refresh Token Entity 생성 및 Access Token만 반환
    public String tokenIssue(UserLoginRequestDto loginRequestDto) {
        User user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        if (passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            String accessToken = tokenProvider.createAccessToken(user);
            String refreshToken = tokenProvider.createRefreshToken();
            refreshTokenService.saveNewRefreshToken(accessToken, refreshToken, user.getId());
            return accessToken;
        } else throw new TodoAppException(ErrorCode.LOGIN_FAILS);
    }
}
