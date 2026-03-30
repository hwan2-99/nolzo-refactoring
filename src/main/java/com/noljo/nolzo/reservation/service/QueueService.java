package com.noljo.nolzo.reservation.service;

import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.entity.QueueStatus;
import com.noljo.nolzo.reservation.entity.ReservationQueueEntry;
import com.noljo.nolzo.reservation.repository.ReservationQueueEntryRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private static final long MAX_ACTIVE_USERS = 50L;
    private static final Duration ENTER_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;
    private final ReservationQueueEntryRepository queueEntryRepository;

    @Transactional
    public void validateQueue(Long eventId, Long memberId) {
        cleanupExpiredActiveUsers(eventId);

        if (hasEnterKey(eventId, memberId)) {
            syncActiveStateIfNeeded(eventId, memberId);
            return;
        }

        long activeCount = getActiveCount(eventId);

        if (activeCount < MAX_ACTIVE_USERS) {
            grantEntrance(eventId, memberId);
            return;
        }

        if (!isAlreadyQueued(eventId, memberId)) {
            addQueue(eventId, memberId);
        } else {
            ensureWaitingState(eventId, memberId);
        }

        Long rank = getQueueRank(eventId, memberId);
        long waitingNumber = rank == null ? 0L : rank + 1L;

        throw new IllegalStateException("현재 예매 대기열에 있습니다. 순번: " + waitingNumber);
    }

    @Transactional
    public void leaveEntrance(Long eventId, Long memberId) {
        redisTemplate.delete(getEnterKey(eventId, memberId));
        redisTemplate.opsForZSet().remove(getActiveKey(eventId), String.valueOf(memberId));

        queueEntryRepository.findByEventIdAndMemberId(eventId, memberId)
                .ifPresent(entry -> {
                    if (entry.getStatus() == QueueStatus.ACTIVE) {
                        entry.leave();
                    }
                });

        log.info("예매 입장 종료 - eventId={}, memberId={}", eventId, memberId);
    }

    @Transactional
    public void markReserved(Long eventId, Long memberId) {
        queueEntryRepository.findByEventIdAndMemberId(eventId, memberId)
                .ifPresent(ReservationQueueEntry::reserve);

        redisTemplate.delete(getEnterKey(eventId, memberId));
        redisTemplate.opsForZSet().remove(getActiveKey(eventId), String.valueOf(memberId));

        log.info("예약 완료 처리 - eventId={}, memberId={}", eventId, memberId);
    }

    @Transactional
    public void processQueue(Long eventId) {
        cleanupExpiredActiveUsers(eventId);

        long activeCount = getActiveCount(eventId);
        long availableSlots = MAX_ACTIVE_USERS - activeCount;

        if (availableSlots <= 0) {
            return;
        }

        Set<Object> nextUsers = redisTemplate.opsForZSet()
                .range(getQueueKey(eventId), 0, availableSlots - 1);

        if (nextUsers == null || nextUsers.isEmpty()) {
            return;
        }

        for (Object userObj : nextUsers) {
            Long memberId = Long.valueOf(String.valueOf(userObj));
            grantEntrance(eventId, memberId);
        }
    }

    public Set<Object> getManagedEventIds() {
        Set<Object> eventIds = redisTemplate.opsForSet().members(getManagedEventsKey());

        if (eventIds == null) {
            return Collections.emptySet();
        }

        return eventIds;
    }

    @Transactional
    public void rebuildRedisFromDb() {
        LocalDateTime now = LocalDateTime.now();

        List<ReservationQueueEntry> expiredActiveEntries =
                queueEntryRepository.findByStatusAndActiveUntilBefore(QueueStatus.ACTIVE, now);

        for (ReservationQueueEntry entry : expiredActiveEntries) {
            entry.expire();
        }

        List<ReservationQueueEntry> waitingEntries =
                queueEntryRepository.findByStatus(QueueStatus.WAITING);

        for (ReservationQueueEntry entry : waitingEntries) {
            redisTemplate.opsForZSet().add(
                    getQueueKey(entry.getEventId()),
                    String.valueOf(entry.getMemberId()),
                    toScore(entry.getQueuedAt())
            );
            redisTemplate.opsForSet().add(getManagedEventsKey(), String.valueOf(entry.getEventId()));
        }

        List<ReservationQueueEntry> activeEntries =
                queueEntryRepository.findByStatusAndActiveUntilAfter(QueueStatus.ACTIVE, now);

        for (ReservationQueueEntry entry : activeEntries) {
            redisTemplate.opsForValue().set(
                    getEnterKey(entry.getEventId(), entry.getMemberId()),
                    "ENTER",
                    Duration.between(now, entry.getActiveUntil())
            );

            redisTemplate.opsForZSet().add(
                    getActiveKey(entry.getEventId()),
                    String.valueOf(entry.getMemberId()),
                    toScore(entry.getActiveUntil())
            );

            redisTemplate.opsForSet().add(getManagedEventsKey(), String.valueOf(entry.getEventId()));
        }

        log.info("Redis 복구 완료 - waiting={}, active={}", waitingEntries.size(), activeEntries.size());
    }

    private void addQueue(Long eventId, Long memberId) {
        memberRepository.getOrThrow(memberId);
        long now = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(getQueueKey(eventId), String.valueOf(memberId), now);
        redisTemplate.opsForSet().add(getManagedEventsKey(), String.valueOf(eventId));

        upsertWaitingEntry(eventId, memberId);

        log.info("예매 대기열 등록 - eventId={}, memberId={}", eventId, memberId);
    }

    private void grantEntrance(Long eventId, Long memberId) {
        long expireAt = System.currentTimeMillis() + ENTER_TTL.toMillis();

        redisTemplate.opsForValue().set(
                getEnterKey(eventId, memberId),
                "ENTER",
                ENTER_TTL
        );

        redisTemplate.opsForZSet().add(getActiveKey(eventId), String.valueOf(memberId), expireAt);
        redisTemplate.opsForZSet().remove(getQueueKey(eventId), String.valueOf(memberId));
        redisTemplate.opsForSet().add(getManagedEventsKey(), String.valueOf(eventId));

        upsertActiveEntry(eventId, memberId, LocalDateTime.now().plus(ENTER_TTL));

        log.info("예매 입장 허용 - eventId={}, memberId={}, expireAt={}", eventId, memberId, expireAt);
    }

    private void upsertWaitingEntry(Long eventId, Long memberId) {
        try {
            ReservationQueueEntry entry = ReservationQueueEntry.waiting(eventId, memberId);
            queueEntryRepository.save(entry);
        } catch (DataIntegrityViolationException e) {
            queueEntryRepository.findByEventIdAndMemberId(eventId, memberId)
                    .ifPresent(existing -> {
                        if (existing.getStatus() != QueueStatus.RESERVED) {
                            existing.backToWaiting();
                        }
                    });
        }
    }

    private void upsertActiveEntry(Long eventId, Long memberId, LocalDateTime activeUntil) {
        ReservationQueueEntry entry = queueEntryRepository.findByEventIdAndMemberId(eventId, memberId)
                .orElseGet(() -> queueEntryRepository.save(
                        ReservationQueueEntry.waiting(eventId, memberId)
                ));

        entry.activate(activeUntil);
    }

    private void ensureWaitingState(Long eventId, Long memberId) {
        queueEntryRepository.findByEventIdAndMemberId(eventId, memberId)
                .ifPresent(entry -> {
                    if (entry.getStatus() != QueueStatus.RESERVED) {
                        entry.backToWaiting();
                    }
                });
    }

    private void syncActiveStateIfNeeded(Long eventId, Long memberId) {
        queueEntryRepository.findByEventIdAndMemberId(eventId, memberId)
                .ifPresent(entry -> {
                    if (entry.getStatus() != QueueStatus.ACTIVE) {
                        entry.activate(LocalDateTime.now().plus(ENTER_TTL));
                    }
                });
    }

    private boolean hasEnterKey(Long eventId, Long memberId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getEnterKey(eventId, memberId)));
    }

    private long getActiveCount(Long eventId) {
        Long count = redisTemplate.opsForZSet().zCard(getActiveKey(eventId));

        if (count == null) {
            return 0L;
        }
        return count;
    }

    private boolean isAlreadyQueued(Long eventId, Long memberId) {
        Long rank = redisTemplate.opsForZSet().rank(getQueueKey(eventId), String.valueOf(memberId));
        return rank != null;
    }

    private Long getQueueRank(Long eventId, Long memberId) {
        return redisTemplate.opsForZSet().rank(getQueueKey(eventId), String.valueOf(memberId));
    }

    private void cleanupExpiredActiveUsers(Long eventId) {
        long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().removeRangeByScore(getActiveKey(eventId), 0, now);

        List<ReservationQueueEntry> expiredEntries =
                queueEntryRepository.findByEventIdAndStatusOrderByQueuedAtAsc(eventId, QueueStatus.ACTIVE);

        LocalDateTime current = LocalDateTime.now();
        for (ReservationQueueEntry entry : expiredEntries) {
            if (entry.getActiveUntil() != null && entry.getActiveUntil().isBefore(current)) {
                entry.expire();
            }
        }
    }

    private double toScore(LocalDateTime time) {
        return time.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private String getQueueKey(Long eventId) {
        return "queue:reservation:event:" + eventId;
    }

    private String getActiveKey(Long eventId) {
        return "active:reservation:event:" + eventId;
    }

    private String getEnterKey(Long eventId, Long memberId) {
        return "queue:reservation:enter:" + eventId + ":" + memberId;
    }

    private String getManagedEventsKey() {
        return "queue:reservation:managed:events";
    }
}
