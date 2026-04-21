package com.noljo.nolzo.schedule.adapter.out.persistence;

import com.noljo.nolzo.schedule.application.port.out.SchedulePersistencePort;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.schedule.repository.ScheduleRepository;
import com.noljo.nolzo.seat.dto.SeatResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulePersistenceAdapter implements SchedulePersistencePort {

    private final ScheduleRepository scheduleRepository;

    @Override
    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public <S extends Schedule> S save(S schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Optional<Schedule> findByEventIdAndShowDateAndShowTime(Long eventId, LocalDate showDate,
                                                                  LocalTime showTime) {
        return scheduleRepository.findByEventIdAndShowDateAndShowTime(eventId, showDate, showTime);
    }

    @Override
    public List<SeatResponse> findSeatResponsesBySchedule(Long eventId, LocalDate showDate, LocalTime showTime) {
        return scheduleRepository.findSeatResponsesBySchedule(eventId, showDate, showTime);
    }
}
