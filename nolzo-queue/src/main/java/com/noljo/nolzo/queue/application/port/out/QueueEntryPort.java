package com.noljo.nolzo.queue.application.port.out;

import com.noljo.nolzo.queue.domain.QueueEntry;
import com.noljo.nolzo.queue.domain.QueueStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueEntryPort {

    Optional<QueueEntry> findByEventIdAndMemberId(Long eventId, Long memberId);

    QueueEntry save(QueueEntry entry);

    List<QueueEntry> findByStatus(QueueStatus status);

    List<QueueEntry> findByEventIdAndStatusOrderByQueuedAtAsc(Long eventId, QueueStatus status);

    List<QueueEntry> findByStatusAndActiveUntilAfter(QueueStatus status, LocalDateTime now);

    List<QueueEntry> findByStatusAndActiveUntilBefore(QueueStatus status, LocalDateTime now);
}
