package com.noljo.nolzo.notification.adapter.message;

import com.noljo.nolzo.notification.domain.event.NotificationBatchRequest;
import java.time.LocalDateTime;
import java.util.List;

public record NotificationBatchRequestMessage(
        Long eventId,
        Long eventScheduleId,
        Long seatId,
        String seatGrade,
        LocalDateTime availableAt,
        List<Long> subscriptionIds
) {

    public static NotificationBatchRequestMessage from(NotificationBatchRequest request) {
        return new NotificationBatchRequestMessage(
                request.eventId(),
                request.eventScheduleId(),
                request.seatId(),
                request.seatGrade(),
                request.availableAt(),
                request.subscriptionIds()
        );
    }

    public NotificationBatchRequest toRequest() {
        return new NotificationBatchRequest(
                eventId,
                eventScheduleId,
                seatId,
                seatGrade,
                availableAt,
                subscriptionIds
        );
    }
}
