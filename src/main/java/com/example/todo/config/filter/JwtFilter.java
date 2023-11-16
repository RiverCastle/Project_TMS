package com.example.todo.config.filter;

import com.example.todo.domain.entity.token.RefreshTokenEntity;
import com.example.todo.domain.entity.user.User;
import com.example.todo.domain.repository.user.UserRepository;
import com.example.todo.exception.ErrorCode;
import com.example.todo.exception.TodoAppException;
import com.example.todo.jwt.RefreshTokenService;
import com.example.todo.jwt.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Header의 Authorization 값이 비어있으면 => Jwt Token을 전송하지 않음 => 로그인 하지 않음
        if(authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Header의 Authorization 값이 'Bearer '로 시작하지 않으면 => 잘못된 토큰
        if(!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authHeader.split(" ")[1];
        try {
            tokenProvider.validToken(accessToken);
        } catch (ExpiredJwtException e) {
            RefreshTokenEntity refreshToken = refreshTokenService.getRefreshTokenByAccessToken(accessToken);
            if (tokenProvider.validateRefreshToken(refreshToken.getRefreshToken())) {
                log.info("Before update : " + accessToken);
                log.info("AccessToken Expired. RefreshToken is Valid.");
                User user = userRepository.findById(refreshToken.getUserId()).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
                log.info("Good. User found : " + user.getUsername());
                accessToken = tokenProvider.createAccessToken(user);
                log.info("Reissued AccessToken");
                refreshTokenService.saveNewAccessTokenInRefreshToken(accessToken, refreshToken);
                log.info("refresh token is updated!");
            } else {
                log.info("Refresh Token is Invalid. Relog in Needed.");
                filterChain.doFilter(request, response);
                return;
            }
        }
        log.info("access token : " + accessToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(tokenProvider.getAuthentication(accessToken));
        SecurityContextHolder.setContext(context);
        response.setHeader("Authorization", "Bearer " + accessToken);
        filterChain.doFilter(request, response);
    }
}
