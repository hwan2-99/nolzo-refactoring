package com.noljo.nolzo.queue.repository;

import com.noljo.nolzo.queue.domain.QueueEntry;
import com.noljo.nolzo.queue.domain.QueueStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {
    Optional<QueueEntry> findByEventIdAndMemberId(Long eventId, Long memberId);

    List<QueueEntry> findByStatus(QueueStatus status);

    List<QueueEntry> findByEventIdAndStatusOrderByQueuedAtAsc(Long eventId, QueueStatus status);

    List<QueueEntry> findByStatusAndActiveUntilAfter(QueueStatus status, LocalDateTime now);

    List<QueueEntry> findByStatusAndActiveUntilBefore(QueueStatus status, LocalDateTime now);

    boolean existsByEventIdAndMemberIdAndStatusIn(Long eventId, Long memberId, List<QueueStatus> statuses);

    long countByEventIdAndStatus(Long eventId, QueueStatus status);
}
