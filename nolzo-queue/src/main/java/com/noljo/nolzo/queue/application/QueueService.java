package com.noljo.nolzo.queue.application;

import com.noljo.nolzo.queue.application.port.out.MemberPort;
import com.noljo.nolzo.queue.application.port.out.QueueEntryPort;
import com.noljo.nolzo.queue.application.port.out.QueueStorePort;
import com.noljo.nolzo.queue.domain.QueueEntry;
import com.noljo.nolzo.queue.domain.QueueStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private static final long MAX_ACTIVE_USERS = 50L;
    private static final Duration ENTER_TTL = Duration.ofMinutes(5);

    private final QueueStorePort queueStorePort;
    private final QueueEntryPort queueEntryPort;
    private final MemberPort memberPort;

    @Transactional
    public void validateQueue(Long eventId, Long memberId) {
        cleanupExpiredActiveUsers(eventId);

        if (maintainExistingEntrance(eventId, memberId)) {
            return;
        }

        if (tryGrantImmediateEntrance(eventId, memberId)) {
            return;
        }

        enterWaitingQueue(eventId, memberId);
    }

    @Transactional
    public void leaveEntrance(Long eventId, Long memberId) {
        queueStorePort.removeEntrance(eventId, memberId);
        queueStorePort.removeActive(eventId, memberId);

        queueEntryPort.findByEventIdAndMemberId(eventId, memberId)
                .ifPresent(entry -> {
                    if (entry.getStatus() == QueueStatus.ACTIVE) {
                        entry.leave();
                    }
                });

        log.info("예매 입장 종료 - eventId={}, memberId={}", eventId, memberId);
    }

    @Transactional
    public void markReserved(Long eventId, Long memberId) {
        queueEntryPort.findByEventIdAndMemberId(eventId, memberId)
                .ifPresent(QueueEntry::reserve);

        queueStorePort.removeEntrance(eventId, memberId);
        queueStorePort.removeActive(eventId, memberId);

        log.info("예약 완료 처리 - eventId={}, memberId={}", eventId, memberId);
    }

    @Transactional
    public void processQueue(Long eventId) {
        cleanupExpiredActiveUsers(eventId);

        long activeCount = queueStorePort.getActiveCount(eventId);
        long availableSlots = MAX_ACTIVE_USERS - activeCount;

        if (availableSlots <= 0) {
            return;
        }

        List<Long> nextUsers = queueStorePort.getNextWaitingMembers(eventId, availableSlots);

        for (Long memberId : nextUsers) {
            grantEntrance(eventId, memberId);
        }
    }

    public Set<Long> getManagedEventIds() {
        return queueStorePort.getManagedEventIds();
    }

    private boolean maintainExistingEntrance(Long eventId, Long memberId) {
        if (!queueStorePort.hasEntrance(eventId, memberId)) {
            return false;
        }

        syncActiveStateIfNeeded(eventId, memberId);
        return true;
    }

    private boolean tryGrantImmediateEntrance(Long eventId, Long memberId) {
        long activeCount = queueStorePort.getActiveCount(eventId);

        if (activeCount >= MAX_ACTIVE_USERS) {
            return false;
        }

        grantEntrance(eventId, memberId);
        return true;
    }

    private void enterWaitingQueue(Long eventId, Long memberId) {
        if (!queueStorePort.isAlreadyQueued(eventId, memberId)) {
            addQueue(eventId, memberId);
        } else {
            ensureWaitingState(eventId, memberId);
        }

        throwWaitingQueueException(eventId, memberId);
    }

    private void throwWaitingQueueException(Long eventId, Long memberId) {
        Long rank = queueStorePort.getQueueRank(eventId, memberId);
        long waitingNumber = rank == null ? 0L : rank + 1L;

        throw new IllegalStateException("현재 예매 대기열에 있습니다. 순번: " + waitingNumber);
    }

    private void addQueue(Long eventId, Long memberId) {
        memberPort.ensureExists(memberId);
        queueStorePort.enqueue(eventId, memberId, System.currentTimeMillis());
        upsertWaitingEntry(eventId, memberId);

        log.info("예매 대기열 등록 - eventId={}, memberId={}", eventId, memberId);
    }

    private void grantEntrance(Long eventId, Long memberId) {
        long expireAt = System.currentTimeMillis() + ENTER_TTL.toMillis();

        queueStorePort.grantEntrance(eventId, memberId, ENTER_TTL, expireAt);
        upsertActiveEntry(eventId, memberId, LocalDateTime.now().plus(ENTER_TTL));

        log.info("예매 입장 허용 - eventId={}, memberId={}, expireAt={}", eventId, memberId, expireAt);
    }

    private void upsertWaitingEntry(Long eventId, Long memberId) {
        try {
            QueueEntry entry = QueueEntry.waiting(eventId, memberId);
            queueEntryPort.save(entry);
        } catch (DataIntegrityViolationException e) {
            queueEntryPort.findByEventIdAndMemberId(eventId, memberId)
                    .ifPresent(QueueEntry::reenterWaiting);
        }
    }

    private void upsertActiveEntry(Long eventId, Long memberId, LocalDateTime activeUntil) {
        QueueEntry entry = queueEntryPort.findByEventIdAndMemberId(eventId, memberId)
                .orElseGet(() -> queueEntryPort.save(
                        QueueEntry.waiting(eventId, memberId)
                ));

        entry.activate(activeUntil);
    }

    private void ensureWaitingState(Long eventId, Long memberId) {
        queueEntryPort.findByEventIdAndMemberId(eventId, memberId)
                .ifPresent(entry -> {
                    if (entry.getStatus() != QueueStatus.WAITING) {
                        entry.backToWaiting();
                    }
                });
    }

    private void syncActiveStateIfNeeded(Long eventId, Long memberId) {
        queueEntryPort.findByEventIdAndMemberId(eventId, memberId)
                .ifPresent(entry -> {
                    if (entry.getStatus() != QueueStatus.ACTIVE) {
                        entry.activate(LocalDateTime.now().plus(ENTER_TTL));
                    }
                });
    }

    private void cleanupExpiredActiveUsers(Long eventId) {
        long now = System.currentTimeMillis();
        queueStorePort.cleanupExpiredActiveUsers(eventId, now);

        List<QueueEntry> expiredEntries =
                queueEntryPort.findByEventIdAndStatusOrderByQueuedAtAsc(eventId, QueueStatus.ACTIVE);

        LocalDateTime current = LocalDateTime.now();
        for (QueueEntry entry : expiredEntries) {
            if (entry.getActiveUntil() != null && entry.getActiveUntil().isBefore(current)) {
                entry.expire();
            }
        }
    }
}
