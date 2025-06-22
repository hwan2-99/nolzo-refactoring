package com.noljo.nolzo.event.dto;

import com.noljo.nolzo.event.dto.internal.ScheduleInfo;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.Schedule.entity.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class EventRequest {
    @NotBlank(message = "제목 지정 필수")
    private String title;

    @NotBlank(message = "장소 지정 필수")
    private String venue;

    private String description;

    private String posterImageUrl;

    @NotNull(message = "시작일 지정 필수")
    private LocalDate startDate;

    @NotNull(message = "종료일 지정 필수")
    private LocalDate endDate;

    private ScheduleInfo scheduleInfo;

    //콤보박스여도 notnull?
    @NotNull(message = "카테고리 지정 필수")
    private EventCategory eventCategory;

    private int runtime;

    private int ageLimit;
    public Event toEntity(Long id) {
        return Event.builder()
                .id(id)
                .title(title)
                .venue(venue)
                .description(description)
                .posterImageUrl(posterImageUrl)
                .startDate(startDate)
                .endDate(endDate)
                .eventCategory(eventCategory)
                .runtime(runtime)
                .ageLimit(ageLimit)
                .rating(0)
                .reviewCount(0)
                .build();
    }

    public Event toEntity() {
        return toEntity(null);
    }
}