package com.noljo.nolzo.notification.domain.event;

import java.time.LocalDateTime;
import java.util.List;

public record NotificationBatchRequest(
        Long eventId,
        Long eventScheduleId,
        Long seatId,
        String seatGrade,
        LocalDateTime availableAt,
        List<Long> subscriptionIds
) {
}
