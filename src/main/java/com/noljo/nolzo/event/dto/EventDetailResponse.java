package com.noljo.nolzo.event.dto;

import com.noljo.nolzo.event.dto.internal.ScheduleInfo;
import com.noljo.nolzo.event.entity.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class EventDetailResponse {
    private Long eventId;
    private String title;
    private String venue;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int ageLimit;
    private String posterUrl;
    private List<ScheduleInfo> schedules;

    static public EventDetailResponse from(List<ScheduleInfo> scheduleInfos, Event event){
        return EventDetailResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .venue(event.getVenue())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .ageLimit(event.getAgeLimit())
                .posterUrl(event.getPosterImageUrl())
                .schedules(scheduleInfos)
                .build();
    }
}
