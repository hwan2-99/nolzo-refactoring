package com.noljo.nolzo.outbox.adapter.out.persistence;

import com.noljo.nolzo.outbox.application.port.out.LoadOutboxEventPort;
import com.noljo.nolzo.outbox.application.port.out.SaveOutboxEventPort;
import com.noljo.nolzo.outbox.domain.OutboxEvent;
import com.noljo.nolzo.outbox.domain.OutboxEventStatus;
import com.noljo.nolzo.outbox.repository.OutboxEventRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventPersistenceAdapter implements SaveOutboxEventPort, LoadOutboxEventPort {

    private final OutboxEventRepository outboxEventRepository;

    @Override
    public <S extends OutboxEvent> S save(S outboxEvent) {
        return outboxEventRepository.save(outboxEvent);
    }

    @Override
    public List<OutboxEvent> findPublishableEvents(int limit) {
        return outboxEventRepository.findByStatusInOrderByIdAsc(
                List.of(OutboxEventStatus.PENDING, OutboxEventStatus.FAILED),
                PageRequest.of(0, limit)
        );
    }
}
