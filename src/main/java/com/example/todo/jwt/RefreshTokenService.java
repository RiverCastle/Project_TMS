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

    public RefreshTokenEntity getRefreshTokenByAccessToken(String accessToken) {
        try {
            // 여기에서 accessToken을 디코딩하여 사용자 정보 등을 얻어올 수 있습니다.
            // 이 예시에서는 단순히 accessToken 자체를 사용하여 Refresh Token을 조회하는 것으로 가정합니다.

            RefreshTokenEntity refreshTokenEntity =
                    refreshTokenRepository.findById(accessToken).orElseThrow(() -> new TodoAppException(ErrorCode.LOGIN_NEEDED));

            return refreshTokenEntity;
        } catch (Exception e) {
            // 예외 처리
            return null;
        }
    }

    public void saveNewAccessTokenInRefreshToken(String reAccessToken, RefreshTokenEntity refreshToken) {
        refreshToken.setAccessToken(reAccessToken);
        refreshTokenRepository.save(refreshToken);

    }

    public void saveNewRefreshToken(String accessToken, String refreshToken, Long userId) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity(accessToken, refreshToken, userId);
        refreshTokenRepository.save(refreshTokenEntity);
    }
}

