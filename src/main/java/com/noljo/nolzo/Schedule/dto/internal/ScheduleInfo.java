package com.noljo.nolzo.Schedule.dto.internal;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.Schedule.entity.Schedule;
import lombok.Builder;
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
