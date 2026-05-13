package com.noljo.nolzo.event.dto;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import java.time.LocalDate;

public record EventRecommendItemResponse(
        Long eventId,
        String title,
        String venue,
        LocalDate startDate,
        LocalDate endDate,
        EventCategory category,
        String recommendationReason
) {
    public static EventRecommendItemResponse of(Event event, String recommendationReason) {
        return new EventRecommendItemResponse(
                event.getId(),
                event.getTitle(),
                event.getVenue(),
                event.getStartDate(),
                event.getEndDate(),
                event.getEventCategory(),
                recommendationReason
        );
    }
}
