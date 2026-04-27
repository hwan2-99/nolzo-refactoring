package com.noljo.nolzo.notification.dto;

import com.noljo.nolzo.notification.domain.NotificationChannel;

public record CreateSeatAvailabilitySubscriptionRequest(
        Long eventId,
        Long eventScheduleId,
        NotificationChannel channel
) {
}
