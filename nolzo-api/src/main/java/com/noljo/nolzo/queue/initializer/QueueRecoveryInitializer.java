package com.noljo.nolzo.queue.initializer;

import com.noljo.nolzo.queue.application.port.in.QueueRecoveryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueRecoveryInitializer {

    private final QueueRecoveryUseCase queueRecoveryService;

    @EventListener(ApplicationReadyEvent.class)
    public void recover() {
        log.info("애플리케이션 기동 완료 - Redis 대기열 복구 시작");
        queueRecoveryService.rebuildRedisFromDb();
        log.info("Redis 대기열 복구 완료");
    }
}
