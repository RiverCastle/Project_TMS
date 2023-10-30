package com.example.todo.dto.user.request;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class UserLoginRequestDto {

    private String username;
    private String password;

    @Builder
    public UserLoginRequestDto(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
}
