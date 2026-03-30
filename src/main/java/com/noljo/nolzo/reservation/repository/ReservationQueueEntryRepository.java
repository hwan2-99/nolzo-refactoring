package com.noljo.nolzo.reservation.repository;

import com.noljo.nolzo.reservation.entity.QueueStatus;
import com.noljo.nolzo.reservation.entity.ReservationQueueEntry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationQueueEntryRepository extends JpaRepository<ReservationQueueEntry, Long> {
    Optional<ReservationQueueEntry> findByEventIdAndMemberId(Long eventId, Long memberId);

    List<ReservationQueueEntry> findByStatus(QueueStatus status);

    List<ReservationQueueEntry> findByEventIdAndStatusOrderByQueuedAtAsc(Long eventId, QueueStatus status);

    List<ReservationQueueEntry> findByStatusAndActiveUntilAfter(QueueStatus status, LocalDateTime now);

    List<ReservationQueueEntry> findByStatusAndActiveUntilBefore(QueueStatus status, LocalDateTime now);

    boolean existsByEventIdAndMemberIdAndStatusIn(Long eventId, Long memberId, List<QueueStatus> statuses);

    long countByEventIdAndStatus(Long eventId, QueueStatus status);
}
