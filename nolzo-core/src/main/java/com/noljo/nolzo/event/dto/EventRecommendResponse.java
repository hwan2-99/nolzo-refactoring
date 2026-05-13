package com.noljo.nolzo.event.dto;

import java.util.List;

public record EventRecommendResponse(
        String originalQuery,
        String message,
        EventRecommendCondition condition,
        List<EventRecommendItemResponse> recommendations
) {
    public static EventRecommendResponse of(
            String originalQuery,
            String message,
            EventRecommendCondition condition,
            List<EventRecommendItemResponse> recommendations
    ) {
        return new EventRecommendResponse(originalQuery, message, condition, recommendations);
    }
}
