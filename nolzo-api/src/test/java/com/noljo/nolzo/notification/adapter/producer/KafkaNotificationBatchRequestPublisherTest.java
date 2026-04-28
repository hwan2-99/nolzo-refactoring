package com.noljo.nolzo.notification.adapter.producer;

import static org.mockito.Mockito.verify;

import com.noljo.nolzo.notification.adapter.message.NotificationBatchRequestMessage;
import com.noljo.nolzo.notification.domain.event.NotificationBatchRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class KafkaNotificationBatchRequestPublisherTest {

    @Mock
    private KafkaTemplate<String, NotificationBatchRequestMessage> kafkaTemplate;

    @InjectMocks
    private KafkaNotificationBatchRequestPublisher kafkaNotificationBatchRequestPublisher;

    @Test
    void 알림_배치_요청을_Kafka_토픽으로_발행한다() {
        ReflectionTestUtils.setField(
                kafkaNotificationBatchRequestPublisher,
                "notificationBatchRequestTopic",
                "notification-batch-requests"
        );

        NotificationBatchRequest request = new NotificationBatchRequest(
                1L,
                10L,
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 25, 12, 0),
                List.of(1L, 2L, 3L)
        );

        kafkaNotificationBatchRequestPublisher.publish(request);

        verify(kafkaTemplate).send(
                "notification-batch-requests",
                "10",
                NotificationBatchRequestMessage.from(request)
        );
    }
}
