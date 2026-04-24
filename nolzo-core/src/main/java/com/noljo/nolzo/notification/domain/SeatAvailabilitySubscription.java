package com.noljo.nolzo.notification.domain;

import com.noljo.nolzo.global.BaseEntity;
import com.noljo.nolzo.seat.entity.SectionPrice;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "seat_availability_subscription",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_subscription_member_event_schedule_grade_channel",
                        columnNames = {"member_id", "event_id", "event_schedule_id", "seat_grade", "channel"}
                )
        }
)
public class SeatAvailabilitySubscription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "event_schedule_id", nullable = false)
    private Long eventScheduleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_grade", nullable = false)
    private SectionPrice seatGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Column(name = "last_notified_at")
    private LocalDateTime lastNotifiedAt;

    public SeatAvailabilitySubscription(
            Long memberId,
            Long eventId,
            Long eventScheduleId,
            SectionPrice seatGrade,
            NotificationChannel channel,
            SubscriptionStatus status
    ) {
        this.memberId = memberId;
        this.eventId = eventId;
        this.eventScheduleId = eventScheduleId;
        this.seatGrade = seatGrade;
        this.channel = channel;
        this.status = status;
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
    }

    public void unsubscribe() {
        this.status = SubscriptionStatus.UNSUBSCRIBED;
    }

    public void updateLastNotifiedAt(LocalDateTime lastNotifiedAt) {
        this.lastNotifiedAt = lastNotifiedAt;
    }
}
