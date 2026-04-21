package com.noljo.nolzo.queue.adapter.out.redis;

import com.noljo.nolzo.queue.application.port.out.QueueStorePort;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisQueueAdapter implements QueueStorePort {

    private final RedisTemplate<String, Object> redisTemplate;
    private final QueueKeyGenerator keyGenerator;

    @Override
    public boolean hasEntrance(Long eventId, Long memberId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(keyGenerator.enterKey(eventId, memberId)));
    }

    @Override
    public long getActiveCount(Long eventId) {
        Long count = redisTemplate.opsForZSet().zCard(keyGenerator.activeKey(eventId));
        return count == null ? 0L : count;
    }

    @Override
    public boolean isAlreadyQueued(Long eventId, Long memberId) {
        return getQueueRank(eventId, memberId) != null;
    }

    @Override
    public Long getQueueRank(Long eventId, Long memberId) {
        return redisTemplate.opsForZSet().rank(keyGenerator.queueKey(eventId), String.valueOf(memberId));
    }

    @Override
    public void enqueue(Long eventId, Long memberId, long queuedAt) {
        redisTemplate.opsForZSet().add(keyGenerator.queueKey(eventId), String.valueOf(memberId), queuedAt);
        redisTemplate.opsForSet().add(keyGenerator.managedEventsKey(), String.valueOf(eventId));
    }

    @Override
    public void grantEntrance(Long eventId, Long memberId, Duration ttl, long expireAt) {
        redisTemplate.opsForValue().set(
                keyGenerator.enterKey(eventId, memberId),
                "ENTER",
                ttl
        );
        redisTemplate.opsForZSet().add(keyGenerator.activeKey(eventId), String.valueOf(memberId), expireAt);
        redisTemplate.opsForZSet().remove(keyGenerator.queueKey(eventId), String.valueOf(memberId));
        redisTemplate.opsForSet().add(keyGenerator.managedEventsKey(), String.valueOf(eventId));
    }

    @Override
    public void removeEntrance(Long eventId, Long memberId) {
        redisTemplate.delete(keyGenerator.enterKey(eventId, memberId));
    }

    @Override
    public void removeActive(Long eventId, Long memberId) {
        redisTemplate.opsForZSet().remove(keyGenerator.activeKey(eventId), String.valueOf(memberId));
    }

    @Override
    public List<Long> getNextWaitingMembers(Long eventId, long count) {
        Set<Object> nextUsers = redisTemplate.opsForZSet()
                .range(keyGenerator.queueKey(eventId), 0, count - 1);

        if (nextUsers == null || nextUsers.isEmpty()) {
            return Collections.emptyList();
        }

        return nextUsers.stream()
                .map(userObj -> Long.valueOf(String.valueOf(userObj)))
                .toList();
    }

    @Override
    public Set<Long> getManagedEventIds() {
        Set<Object> eventIds = redisTemplate.opsForSet().members(keyGenerator.managedEventsKey());

        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptySet();
        }

        return eventIds.stream()
                .map(eventObj -> Long.valueOf(String.valueOf(eventObj)))
                .collect(Collectors.toSet());
    }

    @Override
    public void cleanupExpiredActiveUsers(Long eventId, long now) {
        redisTemplate.opsForZSet().removeRangeByScore(keyGenerator.activeKey(eventId), 0, now);
    }

    @Override
    public void restoreWaiting(Long eventId, Long memberId, double score) {
        redisTemplate.opsForZSet().add(keyGenerator.queueKey(eventId), String.valueOf(memberId), score);
        redisTemplate.opsForSet().add(keyGenerator.managedEventsKey(), String.valueOf(eventId));
    }

    @Override
    public void restoreActive(Long eventId, Long memberId, Duration ttl, double score) {
        redisTemplate.opsForValue().set(
                keyGenerator.enterKey(eventId, memberId),
                "ENTER",
                ttl
        );
        redisTemplate.opsForZSet().add(keyGenerator.activeKey(eventId), String.valueOf(memberId), score);
        redisTemplate.opsForSet().add(keyGenerator.managedEventsKey(), String.valueOf(eventId));
    }
}
