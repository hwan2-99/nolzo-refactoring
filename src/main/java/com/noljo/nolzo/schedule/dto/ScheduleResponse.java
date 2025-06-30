package com.noljo.nolzo.schedule.dto;

import com.noljo.nolzo.schedule.entity.Schedule;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ScheduleResponse {
    private final Long id;
    private final LocalDate showDate;
    private final LocalTime showTime;

    public ScheduleResponse(Long id, LocalDate showDate, LocalTime showTime) {
        this.id = id;
        this.showDate = showDate;
        this.showTime = showTime;
    }

    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getShowDate(),
                schedule.getShowTime()
        );
    }
}
