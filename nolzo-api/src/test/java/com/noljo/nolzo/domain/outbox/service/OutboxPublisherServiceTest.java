package com.noljo.nolzo.domain.outbox.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noljo.nolzo.notification.application.port.out.PublishSeatAvailableEventPort;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import com.noljo.nolzo.outbox.domain.OutboxEvent;
import com.noljo.nolzo.outbox.domain.OutboxEventStatus;
import com.noljo.nolzo.outbox.domain.OutboxEventType;
import com.noljo.nolzo.outbox.repository.OutboxEventRepository;
import com.noljo.nolzo.outbox.service.OutboxPublisherService;
import com.noljo.nolzo.support.annotation.ServiceTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@ServiceTest
class OutboxPublisherServiceTest {

    @Autowired
    private OutboxPublisherService outboxPublisherService;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PublishSeatAvailableEventPort publishSeatAvailableEventPort;

    @Test
    void pending_outbox_이벤트를_발행하면_published_상태로_변경된다() throws Exception {
        SeatAvailableEvent event = new SeatAvailableEvent(
                1L,
                10L,
                100L,
                "1구역",
                LocalDateTime.of(2026, 5, 4, 12, 0)
        );

        OutboxEvent outboxEvent = outboxEventRepository.save(new OutboxEvent(
                "RESERVATION",
                100L,
                OutboxEventType.SEAT_AVAILABLE,
                objectMapper.writeValueAsString(event)
        ));

        outboxPublisherService.publishPendingEvents();

        verify(publishSeatAvailableEventPort).publish(event);

        OutboxEvent saved = outboxEventRepository.findById(outboxEvent.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
    }

    @Test
    void 발행에_실패하면_failed_상태로_변경된다() throws Exception {
        SeatAvailableEvent event = new SeatAvailableEvent(
                1L,
                10L,
                100L,
                "1구역",
                LocalDateTime.of(2026, 5, 4, 12, 0)
        );

        OutboxEvent outboxEvent = outboxEventRepository.save(new OutboxEvent(
                "RESERVATION",
                100L,
                OutboxEventType.SEAT_AVAILABLE,
                objectMapper.writeValueAsString(event)
        ));

        doThrow(new RuntimeException("publish failed"))
                .when(publishSeatAvailableEventPort)
                .publish(event);

        outboxPublisherService.publishPendingEvents();

        OutboxEvent saved = outboxEventRepository.findById(outboxEvent.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
        assertThat(saved.getRetryCount()).isEqualTo(1);
    }
}
