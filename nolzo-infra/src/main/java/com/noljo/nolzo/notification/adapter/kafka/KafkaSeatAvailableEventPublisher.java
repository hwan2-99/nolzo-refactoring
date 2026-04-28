package com.noljo.nolzo.notification.adapter.kafka;

import com.noljo.nolzo.notification.application.port.out.PublishSeatAvailableEventPort;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSeatAvailableEventPublisher implements PublishSeatAvailableEventPort {

    private final KafkaTemplate<String, SeatAvailableEventMessage> kafkaTemplate;

    @Value("${app.kafka.topics.seat-available}")
    private String seatAvailableTopic;

    @Override
    public void publish(SeatAvailableEvent event) {
        SeatAvailableEventMessage message = SeatAvailableEventMessage.from(event);
        String key = String.valueOf(event.eventScheduleId());

        kafkaTemplate.send(seatAvailableTopic, key, message);
        log.info("SeatAvailableEvent sent to Kafka. topic={}, key={}, seatId={}",
                seatAvailableTopic, key, event.seatId());
    }
}
