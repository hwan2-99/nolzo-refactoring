package com.noljo.nolzo.notification.adapter.producer;

import com.noljo.nolzo.notification.adapter.message.NotificationBatchRequestMessage;
import com.noljo.nolzo.notification.application.port.out.PublishNotificationBatchRequestPort;
import com.noljo.nolzo.notification.domain.event.NotificationBatchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNotificationBatchRequestPublisher implements PublishNotificationBatchRequestPort {

    private final KafkaTemplate<String, NotificationBatchRequestMessage> kafkaTemplate;

    @Value("${app.kafka.topics.notification-batch-request}")
    private String notificationBatchRequestTopic;

    @Override
    public void publish(NotificationBatchRequest request) {
        NotificationBatchRequestMessage message = NotificationBatchRequestMessage.from(request);
        String key = String.valueOf(request.eventScheduleId());

        kafkaTemplate.send(notificationBatchRequestTopic, key, message);
        log.info("NotificationBatchRequest sent to Kafka. topic={}, key={}, batchSize={}",
                notificationBatchRequestTopic, key, request.subscriptionIds().size());
    }
}
