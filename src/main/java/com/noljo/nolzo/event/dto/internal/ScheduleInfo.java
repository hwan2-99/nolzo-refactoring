package com.noljo.nolzo.event.dto.internal;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduleInfo {
    private Long scheduleId;
    private Schedule schedule;
    public static ScheduleInfo from(Event event){
        return
        ScheduleInfo.builder()
                .scheduleId(event.getId())
                .schedule(event.getSchedule())
                .build();
    }
}
