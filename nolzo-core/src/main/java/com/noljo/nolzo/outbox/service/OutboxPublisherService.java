package com.noljo.nolzo.outbox.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noljo.nolzo.notification.application.port.out.PublishSeatAvailableEventPort;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import com.noljo.nolzo.outbox.application.port.out.LoadOutboxEventPort;
import com.noljo.nolzo.outbox.application.port.out.SaveOutboxEventPort;
import com.noljo.nolzo.outbox.domain.OutboxEvent;
import com.noljo.nolzo.outbox.domain.OutboxEventType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisherService {

    private final LoadOutboxEventPort loadOutboxEventPort;
    private final SaveOutboxEventPort saveOutboxEventPort;
    private final PublishSeatAvailableEventPort publishSeatAvailableEventPort;
    private final ObjectMapper objectMapper;

    @Value("${app.outbox.publish.batch-size:100}")
    private int batchSize;

    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = loadOutboxEventPort.findPublishableEvents(batchSize);

        for (OutboxEvent outboxEvent : events) {
            publish(outboxEvent);
        }
    }

    private void publish(OutboxEvent outboxEvent) {
        try {
            if (outboxEvent.getEventType() == OutboxEventType.SEAT_AVAILABLE) {
                SeatAvailableEvent event = objectMapper.readValue(
                        outboxEvent.getPayload(),
                        SeatAvailableEvent.class
                );
                publishSeatAvailableEventPort.publish(event);
                outboxEvent.markPublished();
                saveOutboxEventPort.save(outboxEvent);
                return;
            }

            outboxEvent.markFailed("지원하지 않는 이벤트 타입입니다.");
            saveOutboxEventPort.save(outboxEvent);
        } catch (Exception e) {
            log.error("Outbox publish failed. outboxEventId={}", outboxEvent.getId(), e);
            outboxEvent.markFailed(e.getMessage());
            saveOutboxEventPort.save(outboxEvent);
        }
    }
}
