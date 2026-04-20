package com.noljo.nolzo.queue.application.port.out;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public interface QueueStorePort {

    boolean hasEntrance(Long eventId, Long memberId);

    long getActiveCount(Long eventId);

    boolean isAlreadyQueued(Long eventId, Long memberId);

    Long getQueueRank(Long eventId, Long memberId);

    void enqueue(Long eventId, Long memberId, long queuedAt);

    void grantEntrance(Long eventId, Long memberId, Duration ttl, long expireAt);

    void removeEntrance(Long eventId, Long memberId);

    void removeActive(Long eventId, Long memberId);

    List<Long> getNextWaitingMembers(Long eventId, long count);

    Set<Long> getManagedEventIds();

    void cleanupExpiredActiveUsers(Long eventId, long now);

    void restoreWaiting(Long eventId, Long memberId, double score);

    void restoreActive(Long eventId, Long memberId, Duration ttl, double score);
}
