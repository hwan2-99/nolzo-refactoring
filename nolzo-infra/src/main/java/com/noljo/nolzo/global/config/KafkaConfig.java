package com.noljo.nolzo.global.config;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.apache.kafka.clients.admin.NewTopic;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Value("${app.kafka.topics.seat-available}")
    private String seatAvailableTopic;

    @Value("${app.kafka.topics.notification-batch-request}")
    private String notificationBatchRequestTopic;

    @Value("${app.kafka.topics.dlt.seat-available}")
    private String seatAvailableDltTopic;

    @Value("${app.kafka.topics.dlt.notification-batch-request}")
    private String notificationBatchRequestDltTopic;

    @Value("${app.kafka.topic.partitions.seat-available:1}")
    private int seatAvailablePartitions;

    @Value("${app.kafka.topic.partitions.notification-batch-request:1}")
    private int notificationBatchRequestPartitions;

    @Value("${app.kafka.topic.partitions.dlt:1}")
    private int dltPartitions;

    @Value("${app.kafka.topic.replicas.seat-available:1}")
    private short seatAvailableReplicas;

    @Value("${app.kafka.topic.replicas.notification-batch-request:1}")
    private short notificationBatchRequestReplicas;

    @Value("${app.kafka.topic.replicas.dlt:1}")
    private short dltReplicas;

    @Value("${app.kafka.error.retry-attempts:3}")
    private int retryAttempts;

    @Value("${app.kafka.error.initial-interval-ms:1000}")
    private long initialIntervalMs;

    @Value("${app.kafka.error.multiplier:2.0}")
    private double backoffMultiplier;

    @Value("${app.kafka.error.max-interval-ms:10000}")
    private long maxIntervalMs;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory,
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // Kafka consumer가 사용할 기본 factory 설정
        factory.setConsumerFactory(consumerFactory);
        // 레코드 단위 커밋 설정 - 메시지별 처리 성공/실패 기준으로 offset 관리
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        // 처리 실패한 메시지를 DLT로 발행하기 위한 recoverer 설정
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(resolveDltTopic(record.topic()), record.partition())
        );

        // 재시도 간격을 점진적으로 늘리는 지수 백오프 설정
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(retryAttempts);
        backOff.setInitialInterval(initialIntervalMs);
        backOff.setMultiplier(backoffMultiplier);
        backOff.setMaxInterval(maxIntervalMs);

        // 공통 에러 핸들러 설정 - 재시도 후에도 실패하면 DLT로 전달
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        // 비즈니스 유효성 예외는 재시도해도 의미가 없으므로 즉시 실패 처리
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    @ConditionalOnProperty(name = "app.kafka.admin.enabled", havingValue = "true", matchIfMissing = true)
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    @ConditionalOnProperty(name = "app.kafka.admin.enabled", havingValue = "true", matchIfMissing = true)
    public NewTopic seatAvailableEventTopic() {
        return TopicBuilder.name(seatAvailableTopic)
                .partitions(seatAvailablePartitions)
                .replicas(seatAvailableReplicas)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.kafka.admin.enabled", havingValue = "true", matchIfMissing = true)
    public NewTopic notificationBatchRequestTopic() {
        return TopicBuilder.name(notificationBatchRequestTopic)
                .partitions(notificationBatchRequestPartitions)
                .replicas(notificationBatchRequestReplicas)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.kafka.admin.enabled", havingValue = "true", matchIfMissing = true)
    public NewTopic seatAvailableDltEventTopic() {
        return TopicBuilder.name(seatAvailableDltTopic)
                .partitions(dltPartitions)
                .replicas(dltReplicas)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.kafka.admin.enabled", havingValue = "true", matchIfMissing = true)
    public NewTopic notificationBatchRequestDltTopic() {
        return TopicBuilder.name(notificationBatchRequestDltTopic)
                .partitions(dltPartitions)
                .replicas(dltReplicas)
                .build();
    }

    private String resolveDltTopic(String topic) {
        if (seatAvailableTopic.equals(topic)) {
            return seatAvailableDltTopic;
        }

        if (notificationBatchRequestTopic.equals(topic)) {
            return notificationBatchRequestDltTopic;
        }

        return topic + "-dlt";
    }
}
