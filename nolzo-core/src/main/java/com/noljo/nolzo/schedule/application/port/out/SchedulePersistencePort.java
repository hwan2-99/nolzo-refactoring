package com.noljo.nolzo.schedule.application.port.out;

import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.seat.dto.SeatResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SchedulePersistencePort {

    Optional<Schedule> findById(Long id);

    <S extends Schedule> S save(S schedule);

    Optional<Schedule> findByEventIdAndShowDateAndShowTime(
            Long eventId,
            LocalDate showDate,
            LocalTime showTime
    );

    List<SeatResponse> findSeatResponsesBySchedule(
            Long eventId,
            LocalDate showDate,
            LocalTime showTime
    );
}
