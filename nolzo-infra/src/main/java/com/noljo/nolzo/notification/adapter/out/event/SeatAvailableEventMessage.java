package com.noljo.nolzo.notification.adapter.out.event;

import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import java.time.LocalDateTime;

public record SeatAvailableEventMessage(
        Long eventId,
        Long eventScheduleId,
        Long seatId,
        String seatGrade,
        LocalDateTime availableAt
) {

    public static SeatAvailableEventMessage from(SeatAvailableEvent event) {
        return new SeatAvailableEventMessage(
                event.eventId(),
                event.eventScheduleId(),
                event.seatId(),
                event.seatGrade(),
                event.availableAt()
        );
    }
}
