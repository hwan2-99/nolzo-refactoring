package com.noljo.nolzo.auth.service;

import com.noljo.nolzo.auth.application.port.in.RefreshTokenCleanupUseCase;
import com.noljo.nolzo.auth.application.port.out.RefreshTokenPersistencePort;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenCleanupService implements RefreshTokenCleanupUseCase {

    private final RefreshTokenPersistencePort refreshTokenPersistencePort;

    @Override
    @Transactional
    public void cleanupExpiredRefreshTokens(LocalDateTime now) {
        refreshTokenPersistencePort.deleteAllByExpiryDateBefore(now);
    }
}
