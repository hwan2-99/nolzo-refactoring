package com.noljo.nolzo.notification.adapter.kafka;

import com.noljo.nolzo.notification.application.port.in.HandleSeatAvailableUseCase;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.consumer.enabled", havingValue = "true")
public class SeatAvailableEventConsumer {

    private final HandleSeatAvailableUseCase handleSeatAvailableUseCase;

    @KafkaListener(
            topics = "${app.kafka.topics.seat-available}",
            groupId = "${app.kafka.consumer.group-id}"
    )
    public void consume(SeatAvailableEventMessage message) {
        log.info("SeatAvailableEvent received from Kafka. scheduleId={}, seatId={}",
                message.eventScheduleId(), message.seatId());

        handleSeatAvailableUseCase.handle(new SeatAvailableEvent(
                message.eventId(),
                message.eventScheduleId(),
                message.seatId(),
                message.seatGrade(),
                message.availableAt()
        ));
    }
}
