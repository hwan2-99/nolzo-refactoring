package com.noljo.nolzo.domain.Reservation.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.service.ReservationService;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ServiceTest
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private EventRepository eventRepository;


    @Test
    public void 공연에_대한_날짜_시간_선택할_수_있다() throws Exception {
        //given
        Event event = eventRepository.save(EventFixture.캣츠());

        //when
        EventDateTimeResponse response = reservationService.chooseEventDateTime(event.getId());

        //then
        assertNotNull(response);
        assertEquals(event.getId(), response.getId());
        assertNotNull(response.getShowdate());
        assertNotNull(response.getShowTime());
        }
}
