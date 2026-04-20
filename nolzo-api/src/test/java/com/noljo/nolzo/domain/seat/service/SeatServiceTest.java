package com.noljo.nolzo.domain.seat.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.schedule.application.port.out.SchedulePersistencePort;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.application.port.out.SeatPersistencePort;
import com.noljo.nolzo.seat.service.SeatService;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.ScheduleFixture;
import com.noljo.nolzo.support.fixture.SeatFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
public class SeatServiceTest {
    @Autowired
    private SeatPersistencePort seatRepository;
    @Autowired
    private EventPersistencePort eventRepository;
    @Autowired
    private SchedulePersistencePort scheduleRepository;
    @Autowired
    private SeatService seatService;

    @Test
    void 좌석은_저장_가능하다() {
        Event event = EventFixture.캣츠();
        eventRepository.save(event);
        Schedule schedule = ScheduleFixture.공연_스케쥴(event);
        scheduleRepository.save(schedule);
        Seat seat = SeatFixture.일반좌석(schedule);
        seatRepository.save(seat);
        assertThat(seatRepository.findAll()).hasSize(1);
    }

    @Test
    void 좌석_한번에_생성_가능하다() {
        Event event = EventFixture.캣츠();
        eventRepository.save(event);
        Schedule schedule = ScheduleFixture.공연_스케쥴(event);
        scheduleRepository.save(schedule);

        seatService.createSeats(schedule.getId());
        assertThat(seatRepository.findAll()).hasSize(1000);
    }
}
