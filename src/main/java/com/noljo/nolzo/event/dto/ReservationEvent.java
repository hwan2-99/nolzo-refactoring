package com.noljo.nolzo.event.dto;

import com.noljo.nolzo.Schedule.entity.Schedule;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.reservation.entity.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
}
