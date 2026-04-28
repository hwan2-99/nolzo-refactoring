package com.noljo.nolzo.notification.adapter.kafka;

import com.noljo.nolzo.notification.application.port.in.HandleNotificationBatchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.consumer.enabled", havingValue = "true")
public class NotificationBatchRequestConsumer {

    private final HandleNotificationBatchUseCase handleNotificationBatchUseCase;

    @KafkaListener(
            topics = "${app.kafka.topics.notification-batch-request}",
            groupId = "${app.kafka.consumer.group-id}"
    )
    public void consume(NotificationBatchRequestMessage message) {
        log.info("NotificationBatchRequest received from Kafka. scheduleId={}, batchSize={}",
                message.eventScheduleId(), message.subscriptionIds().size());

        handleNotificationBatchUseCase.handle(message.toRequest());
    }
}
