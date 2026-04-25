package com.noljo.nolzo.notification.domain.event;

import java.time.LocalDateTime;

public record SeatAvailableEvent(
        Long eventId,
        Long eventScheduleId,
        Long seatId,
        String seatGrade,
        LocalDateTime availableAt
) {
}
