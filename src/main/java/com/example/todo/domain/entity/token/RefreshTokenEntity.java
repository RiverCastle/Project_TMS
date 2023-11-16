package com.example.todo.domain.entity.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash(value = "refreshToken", timeToLive = 14400)
public class RefreshTokenEntity {

    @Id
    private String accessToken;
    private String refreshToken;
    private Long userId;

    public RefreshTokenEntity(String accessToken, String refreshToken, Long userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
