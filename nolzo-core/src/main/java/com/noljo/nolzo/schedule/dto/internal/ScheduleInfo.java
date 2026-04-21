package com.noljo.nolzo.schedule.dto.internal;

import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleInfo {
    private Long id;
    private LocalDate showDate;
    private LocalTime showTime;
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;

    public ScheduleInfo(LocalDate showDate, LocalTime showTime, LocalDateTime reservationStart, LocalDateTime reservationEnd){
        this.showDate=showDate;
        this.showTime=showTime;
        this.reservationStart=reservationStart;
        this.reservationEnd=reservationEnd;
    }
    public ScheduleInfo(LocalDate showDate, LocalTime showTime, LocalDateTime reservationStart,LocalDateTime reservationEnd, Long id){
        this.showDate=showDate;
        this.showTime=showTime;
        this.reservationStart=reservationStart;
        this.reservationEnd=reservationEnd;
        this.id=id;
    }
}
