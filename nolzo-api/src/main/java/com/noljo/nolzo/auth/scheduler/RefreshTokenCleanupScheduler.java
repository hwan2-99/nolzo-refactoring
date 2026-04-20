package com.noljo.nolzo.auth.scheduler;

import com.noljo.nolzo.auth.application.port.in.RefreshTokenCleanupUseCase;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenCleanupUseCase refreshTokenCleanupUseCase;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredRefreshTokens() {
        LocalDateTime now = LocalDateTime.now();
        refreshTokenCleanupUseCase.cleanupExpiredRefreshTokens(now);
    }
}
