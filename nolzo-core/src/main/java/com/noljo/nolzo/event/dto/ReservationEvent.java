package com.noljo.nolzo.event.dto;

import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.event.entity.Event;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReservationEvent {

    private Long id;
    private String title;
    private String venue;
    private LocalDate date;
    private LocalTime time;
    private String image;

    public static ReservationEvent from(Event event, Schedule schedule) {
        return ReservationEvent.builder()
                .id(event.getId())
                .title(event.getTitle())
                .venue(event.getVenue())
                .date(schedule.getShowDate())
                .time(schedule.getShowTime())
                .image(event.getPosterImageUrl())
                .build();
    }

    public static ReservationEvent fromDetails(Event event, Schedule schedule) {
        return ReservationEvent.builder()
                .id(event.getId())
                .title(event.getTitle())
                .venue(event.getVenue())
                .date(schedule.getShowDate())
                .time(schedule.getShowTime())
                .build();
    }
}
