package com.example.todo.jwt;

import com.example.todo.domain.entity.token.RefreshTokenEntity;
import com.example.todo.domain.repository.token.RefreshTokenRepository;
import com.example.todo.exception.ErrorCode;
import com.example.todo.exception.TodoAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // access token 으로 refreshToken 객체 조회하기
    public RefreshTokenEntity getRefreshTokenByAccessToken(String accessToken) {
        RefreshTokenEntity refreshTokenEntity =
                refreshTokenRepository.findById(accessToken).orElseThrow(() -> new TodoAppException(ErrorCode.LOGIN_NEEDED));
        return refreshTokenEntity;
    }

    // access token 재발급 후 리프레시 토큰 객체 업데이트
    public void saveNewAccessTokenInRefreshToken(String reAccessToken, RefreshTokenEntity refreshToken) {
        refreshToken.setAccessToken(reAccessToken);
        refreshTokenRepository.save(refreshToken);

    }

    // 로그인 시 리프레시 토큰 객체 생성 및 저장
    public void saveNewRefreshToken(String accessToken, String refreshToken, Long userId) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity(accessToken, refreshToken, userId);
        refreshTokenRepository.save(refreshTokenEntity);
    }
}

