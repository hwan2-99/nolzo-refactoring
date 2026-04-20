package com.noljo.nolzo.queue.application;

import com.noljo.nolzo.queue.application.port.out.QueueEntryPort;
import com.noljo.nolzo.queue.application.port.out.QueueStorePort;
import com.noljo.nolzo.queue.domain.QueueEntry;
import com.noljo.nolzo.queue.domain.QueueStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueRecoveryService {

    private final QueueEntryPort queueEntryPort;
    private final QueueStorePort queueStorePort;

    @Transactional
    public void rebuildRedisFromDb() {
        LocalDateTime now = LocalDateTime.now();
        expireStaleActiveEntries(now);
        List<QueueEntry> waitingEntries = restoreWaitingEntries();
        List<QueueEntry> activeEntries = restoreActiveEntries(now);

        log.info("Redis 복구 완료 - waiting={}, active={}", waitingEntries.size(), activeEntries.size());
    }

    private void expireStaleActiveEntries(LocalDateTime now) {
        List<QueueEntry> expiredActiveEntries =
                queueEntryPort.findByStatusAndActiveUntilBefore(QueueStatus.ACTIVE, now);

        for (QueueEntry entry : expiredActiveEntries) {
            entry.expire();
        }
    }

    private List<QueueEntry> restoreWaitingEntries() {
        List<QueueEntry> waitingEntries = queueEntryPort.findByStatus(QueueStatus.WAITING);

        for (QueueEntry entry : waitingEntries) {
            queueStorePort.restoreWaiting(
                    entry.getEventId(),
                    entry.getMemberId(),
                    toScore(entry.getQueuedAt())
            );
        }

        return waitingEntries;
    }

    private List<QueueEntry> restoreActiveEntries(LocalDateTime now) {
        List<QueueEntry> activeEntries =
                queueEntryPort.findByStatusAndActiveUntilAfter(QueueStatus.ACTIVE, now);

        for (QueueEntry entry : activeEntries) {
            queueStorePort.restoreActive(
                    entry.getEventId(),
                    entry.getMemberId(),
                    Duration.between(now, entry.getActiveUntil()),
                    toScore(entry.getActiveUntil())
            );
        }

        return activeEntries;
    }

    private double toScore(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
