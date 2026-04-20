package com.noljo.nolzo.schedule.dto;

import com.noljo.nolzo.schedule.entity.Schedule;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class ScheduleResponse {
    private final Long id;
    private final LocalDate showDate;
    private final LocalTime showTime;
    private final LocalDateTime reservationStart;
    private final LocalDateTime reservationEnd;

    public ScheduleResponse(Long id, LocalDate showDate, LocalTime showTime, LocalDateTime reservationStart,LocalDateTime reservationEnd) {
        this.id = id;
        this.showDate = showDate;
        this.showTime = showTime;
        this.reservationStart=reservationStart;
        this.reservationEnd=reservationEnd;
    }

    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getShowDate(),
                schedule.getShowTime(),
                schedule.getReservationStart(),
                schedule.getReservationEnd()
        );
    }
}
