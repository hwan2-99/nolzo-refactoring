package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.schedule.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public enum ScheduleFixture {
    공연스케쥴(LocalDate.of(2025, 6, 24), LocalTime.of(21, 0));

    private LocalDate showDate;

    private LocalTime showTime;

    ScheduleFixture(LocalDate showDate, LocalTime showTime) {
        this.showDate = showDate;
        this.showTime = showTime;
    }

    public static Schedule 공연_스케쥴(Event event) {
        return new Schedule(공연스케쥴.showDate, 공연스케쥴.showTime, event);
    }
}
