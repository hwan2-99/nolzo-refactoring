package com.noljo.nolzo.reservation.dto;

import com.noljo.nolzo.event.entity.Event;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class EventDateTimeResponse {

    private Long id;
    private LocalDate showdate;
    private LocalTime showTime;

    private EventDateTimeResponse(Long id, LocalDate showdate, LocalTime showTime) {
        this.id = id;
        this.showdate = showdate;
        this.showTime = showTime;
    }

    public static EventDateTimeResponse fromEvent(Event event){
        return new EventDateTimeResponse(
                event.getId(),
                event.getSchedule().getShowDate(),
                event.getSchedule().getShowTime()
        );
    }
}
