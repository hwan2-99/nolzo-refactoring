package com.noljo.nolzo.notification.adapter.producer;

import static org.mockito.Mockito.verify;

import com.noljo.nolzo.notification.adapter.message.SeatAvailableEventMessage;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class KafkaSeatAvailableEventPublisherTest {

    @Mock
    private KafkaTemplate<String, SeatAvailableEventMessage> kafkaTemplate;

    @InjectMocks
    private KafkaSeatAvailableEventPublisher kafkaSeatAvailableEventPublisher;

    @Test
    void 빈자리_이벤트를_Kafka_토픽으로_발행한다() {
        ReflectionTestUtils.setField(
                kafkaSeatAvailableEventPublisher,
                "seatAvailableTopic",
                "seat-available-events"
        );

        SeatAvailableEvent event = new SeatAvailableEvent(
                1L,
                10L,
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 25, 12, 0)
        );

        kafkaSeatAvailableEventPublisher.publish(event);

        verify(kafkaTemplate).send(
                "seat-available-events",
                "10",
                SeatAvailableEventMessage.from(event)
        );
    }
}
