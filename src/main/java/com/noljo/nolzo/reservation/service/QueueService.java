package com.noljo.nolzo.reservation.service;

import com.noljo.nolzo.member.repository.MemberRepository;
import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private static final long MAX_ACTIVE_USERS = 50L;
    private static final Duration ENTER_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;

    public void validateQueue(Long eventId, Long memberId) {
        cleanupExpiredActiveUsers(eventId);

        if (hasEnterKey(eventId, memberId)) {
            return;
        }

        long activeCount = getActiveCount(eventId);
        if (activeCount < MAX_ACTIVE_USERS) {
            grantEntrance(eventId, memberId);
            return;
        }

        if (!isAlreadyQueued(eventId, memberId)) {
            addQueue(eventId, memberId);
        }

        Long rank = getQueueRank(eventId, memberId);
        long waitingNumber = 0L;

        if (rank != null) {
            waitingNumber = rank + 1L;
        }

        throw new IllegalStateException("현재 예매 대기열에 있습니다. 순번: " + waitingNumber);
    }

    public void leaveEntrance(Long eventId, Long memberId) {
        redisTemplate.delete(getEnterKey(eventId, memberId));
        redisTemplate.opsForZSet().remove(getActiveKey(eventId), String.valueOf(memberId));

        log.info("예매 입장 종료 - eventId={}, memberId={}", eventId, memberId);
    }

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

    private void addQueue(Long eventId, Long memberId) {
        String memberName = memberRepository.getOrThrow(memberId).getName();
        long now = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(getQueueKey(eventId), String.valueOf(memberId), now);
        redisTemplate.opsForSet().add(getManagedEventsKey(), String.valueOf(eventId));

        log.info("예매 대기열 등록 - eventId={}, memberId={}, memberName={}", eventId, memberId, memberName);
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

        log.info("예매 입장 허용 - eventId={}, memberId={}, expireAt={}", eventId, memberId, expireAt);
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
