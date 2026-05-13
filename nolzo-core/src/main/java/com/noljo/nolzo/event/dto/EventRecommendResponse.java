package com.noljo.nolzo.event.dto;

import java.util.List;

public record EventRecommendResponse(
        String originalQuery,
        EventRecommendCondition condition,
        List<EventRecommendItemResponse> recommendations
) {
    public static EventRecommendResponse of(
            String originalQuery,
            EventRecommendCondition condition,
            List<EventRecommendItemResponse> recommendations
    ) {
        return new EventRecommendResponse(originalQuery, condition, recommendations);
    }
}
