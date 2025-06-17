package com.noljo.nolzo.event.dto;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class EventResponseDto {

    private Long id;
    private String title;
    private String venue;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private EventCategory eventCategory;
    private int ageLimit;
    private int rating;
    private int reviewCount;

    public static EventResponseDto from(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .venue(event.getVenue())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .eventCategory(event.getEventCategory())
                .ageLimit(event.getAgeLimit())
                .rating(event.getRating())
                .reviewCount(event.getReviewCount())
                .build();
    }
}