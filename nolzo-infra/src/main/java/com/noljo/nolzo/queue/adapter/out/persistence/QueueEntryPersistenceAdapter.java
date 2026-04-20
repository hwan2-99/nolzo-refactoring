package com.noljo.nolzo.queue.adapter.out.persistence;

import com.noljo.nolzo.queue.application.port.out.QueueEntryPersistencePort;
import com.noljo.nolzo.queue.domain.QueueEntry;
import com.noljo.nolzo.queue.domain.QueueStatus;
import com.noljo.nolzo.queue.repository.QueueEntryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueEntryPersistenceAdapter implements QueueEntryPersistencePort {

    private final QueueEntryRepository queueEntryRepository;

    @Override
    public Optional<QueueEntry> findByEventIdAndMemberId(Long eventId, Long memberId) {
        return queueEntryRepository.findByEventIdAndMemberId(eventId, memberId);
    }

    @Override
    public QueueEntry save(QueueEntry entry) {
        return queueEntryRepository.save(entry);
    }

    @Override
    public List<QueueEntry> findByStatus(QueueStatus status) {
        return queueEntryRepository.findByStatus(status);
    }

    @Override
    public List<QueueEntry> findByEventIdAndStatusOrderByQueuedAtAsc(Long eventId, QueueStatus status) {
        return queueEntryRepository.findByEventIdAndStatusOrderByQueuedAtAsc(eventId, status);
    }

    @Override
    public List<QueueEntry> findByStatusAndActiveUntilAfter(QueueStatus status, LocalDateTime now) {
        return queueEntryRepository.findByStatusAndActiveUntilAfter(status, now);
    }

    @Override
    public List<QueueEntry> findByStatusAndActiveUntilBefore(QueueStatus status, LocalDateTime now) {
        return queueEntryRepository.findByStatusAndActiveUntilBefore(status, now);
    }
}
