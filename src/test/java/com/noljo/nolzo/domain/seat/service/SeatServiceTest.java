package com.noljo.nolzo.domain.seat.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.repository.SeatRepository;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.SeatFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
public class SeatServiceTest {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private EventRepository eventRepository;

    @Test
    void 좌석은_저장_가능하다() {
        Event event = EventFixture.캣츠();
        eventRepository.save(event);
        Seat seat = SeatFixture.일반좌석(event);
        seatRepository.save(seat);
        assertThat(seatRepository.findAll()).hasSize(1);
    }
}
