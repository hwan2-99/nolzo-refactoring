package com.noljo.nolzo.event.dto;

public record EventRecommendCondition(
        String region,
        String dateRange,
        Integer maxPrice,
        String category,
        String mood
) {
}
