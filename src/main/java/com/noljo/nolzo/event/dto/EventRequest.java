package com.noljo.nolzo.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.noljo.nolzo.schedule.dto.internal.ScheduleInfo;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.schedule.entity.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @NotNull(message = "카테고리 지정 필수")
    private EventCategory eventCategory;

    private int runtime;

    private int ageLimit;

    @NotNull (message = "예약시작시점 지정 필수")
    private LocalDateTime reservationStart;
    @NotNull (message = "예약종료시점 지정 필수")
    private LocalDateTime reservationEnd;



    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<ScheduleInfo> schedule;

    public Event toEntity(String posterImageUrl) {
        Event event = Event.builder()
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
                .reservationStart(reservationStart)
                .reservationEnd(reservationEnd)
                .build();

        schedule.forEach(req->{
            Schedule schedule = Schedule.builder()
                    .showDate(req.getShowDate())
                    .showTime(req.getShowTime())
                    .build();
            schedule.setEvent(event);
            event.addSchedule(schedule);
        });
        return event;
    }

    public Event toEntity() {
        return toEntity(null);
    }
}
