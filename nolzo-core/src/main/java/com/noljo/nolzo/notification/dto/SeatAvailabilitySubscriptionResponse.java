package com.noljo.nolzo.notification.dto;

import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.seat.entity.SectionPrice;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatAvailabilitySubscriptionResponse {

    private Long id;
    private Long memberId;
    private Long eventId;
    private Long eventScheduleId;
    private SectionPrice seatGrade;
    private NotificationChannel channel;
    private SubscriptionStatus status;
    private LocalDateTime lastNotifiedAt;
    private LocalDateTime createdAt;

    public static SeatAvailabilitySubscriptionResponse from(SeatAvailabilitySubscription subscription) {
        return SeatAvailabilitySubscriptionResponse.builder()
                .id(subscription.getId())
                .memberId(subscription.getMemberId())
                .eventId(subscription.getEventId())
                .eventScheduleId(subscription.getEventScheduleId())
                .seatGrade(subscription.getSeatGrade())
                .channel(subscription.getChannel())
                .status(subscription.getStatus())
                .lastNotifiedAt(subscription.getLastNotifiedAt())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}
