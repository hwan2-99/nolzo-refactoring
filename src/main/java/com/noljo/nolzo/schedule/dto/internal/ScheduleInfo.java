package com.noljo.nolzo.schedule.dto.internal;

import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleInfo {
    private LocalDate showDate;
    private LocalTime showTime;
    private LocalDateTime reservationStart;

    public ScheduleInfo(LocalDate showDate, LocalTime showTime, LocalDateTime reservationStart){
        this.showDate=showDate;
        this.showTime=showTime;
        this.reservationStart=reservationStart;
    }
}
