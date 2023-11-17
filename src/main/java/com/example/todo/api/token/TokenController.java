package com.example.todo.api.token;

import com.example.todo.config.filter.JwtTokenDto;
import com.example.todo.dto.user.request.UserLoginRequestDto;
import com.example.todo.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/token")
@RestController
public class TokenController {
    private final TokenService tokenService;
    @PostMapping
    public JwtTokenDto issueJWT(@RequestBody UserLoginRequestDto loginRequestDto) {
        String jwt = tokenService.tokenIssue(loginRequestDto);
        JwtTokenDto jwtTokenDto = new JwtTokenDto();
        jwtTokenDto.setToken(jwt);
        return jwtTokenDto;
    }
}
