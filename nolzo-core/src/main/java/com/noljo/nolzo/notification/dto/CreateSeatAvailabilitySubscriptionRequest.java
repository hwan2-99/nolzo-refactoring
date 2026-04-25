package com.noljo.nolzo.notification.dto;

import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.seat.entity.SectionPrice;

public record CreateSeatAvailabilitySubscriptionRequest(
        Long eventId,
        Long eventScheduleId,
        SectionPrice seatGrade,
        NotificationChannel channel
) {
}
