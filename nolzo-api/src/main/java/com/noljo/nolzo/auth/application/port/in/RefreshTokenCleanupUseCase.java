package com.noljo.nolzo.auth.application.port.in;

import java.time.LocalDateTime;

public interface RefreshTokenCleanupUseCase {

    void cleanupExpiredRefreshTokens(LocalDateTime now);
}
