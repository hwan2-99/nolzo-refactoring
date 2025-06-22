package com.noljo.nolzo.event.dto;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.Schedule.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class EventResponse {

    private Long id;
    private String title;
    private String venue;
    private String description;
    private String posterImageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Schedule> schedule;
    private EventCategory eventCategory;
    private int runtime;
    private int ageLimit;
    private int rating;
    private int reviewCount;

    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .venue(event.getVenue())
                .description(event.getDescription())
                .posterImageUrl(event.getPosterImageUrl())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .eventCategory(event.getEventCategory())
                .runtime(event.getRuntime())
                .ageLimit(event.getAgeLimit())
                .rating(event.getRating())
                .reviewCount(event.getReviewCount())
                .schedule(event.getSchedules())
                .build();
    }
}