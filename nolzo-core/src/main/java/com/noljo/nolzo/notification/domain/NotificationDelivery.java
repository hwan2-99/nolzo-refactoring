package com.noljo.nolzo.notification.domain;

import com.noljo.nolzo.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationDelivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_delivery_id")
    private Long id;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "event_schedule_id", nullable = false)
    private Long eventScheduleId;

    @Column(name = "seat_grade", nullable = false)
    private String seatGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationDeliveryStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    public NotificationDelivery(
            Long subscriptionId,
            Long memberId,
            Long eventId,
            Long eventScheduleId,
            String seatGrade,
            NotificationChannel channel,
            String recipient,
            NotificationDeliveryStatus status
    ) {
        this.subscriptionId = subscriptionId;
        this.memberId = memberId;
        this.eventId = eventId;
        this.eventScheduleId = eventScheduleId;
        this.seatGrade = seatGrade;
        this.channel = channel;
        this.recipient = recipient;
        this.status = status;
    }

    public NotificationDelivery(
            Long subscriptionId,
            Long memberId,
            Long eventId,
            Long eventScheduleId,
            String seatGrade,
            NotificationChannel channel,
            String recipient,
            LocalDateTime sentAt
    ) {
        this(subscriptionId, memberId, eventId, eventScheduleId, seatGrade, channel, recipient,
                NotificationDeliveryStatus.SENT);
        this.sentAt = sentAt;
    }

    public NotificationDelivery(
            Long subscriptionId,
            Long memberId,
            Long eventId,
            Long eventScheduleId,
            String seatGrade,
            NotificationChannel channel,
            String recipient,
            String failureReason
    ) {
        this(subscriptionId, memberId, eventId, eventScheduleId, seatGrade, channel, recipient,
                NotificationDeliveryStatus.FAILED);
        this.failureReason = failureReason;
    }
}
