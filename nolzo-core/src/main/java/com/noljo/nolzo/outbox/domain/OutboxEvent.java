package com.noljo.nolzo.outbox.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;

    private Long aggregateId;

    @Enumerated(EnumType.STRING)
    private OutboxEventType eventType;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status;

    private int retryCount;

    @Column(columnDefinition = "TEXT")
    private String lastErrorMessage;

    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    protected OutboxEvent() {
    }

    public OutboxEvent(
            String aggregateType,
            Long aggregateId,
            OutboxEventType eventType,
            String payload
    ) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxEventStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void markPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.lastErrorMessage = null;
    }

    public void markFailed(String message) {
        this.status = OutboxEventStatus.FAILED;
        this.retryCount++;
        this.lastErrorMessage = message;
    }

    public Long getId() {
        return id;
    }

    public OutboxEventType getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxEventStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }
}
