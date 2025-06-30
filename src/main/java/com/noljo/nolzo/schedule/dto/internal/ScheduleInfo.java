package com.noljo.nolzo.schedule.dto.internal;

import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ScheduleInfo {
    private LocalDate showDate;
    private LocalTime showTime;

    public ScheduleInfo(LocalDate showDate, LocalTime showTime){
        this.showDate=showDate;
        this.showTime=showTime;
    }
}
