package com.noljo.nolzo.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.reservation.dto.ReservationRequest;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.reservation.service.ReservationService;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.repository.SeatRepository;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.*;
import com.noljo.nolzo.ticket.repository.TicketRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@ServiceTest
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void 같은_좌석은_동시에_접근이_불가능하다() throws InterruptedException {
        Member member = memberRepository.save(MemberFixture.회원());
        Member anotherMember = memberRepository.save(MemberFixture.회투());

        Event event = eventRepository.save(EventFixture.캣츠());

        Seat seat1 = seatRepository.save(SeatFixture.일반좌석(event));
        Seat seat2 = seatRepository.save(SeatFixture.일반좌석2(event));

        List<Seat> seats = seatRepository.findAll();
        ReservationRequest request = new ReservationRequest(event.getId(), seats);

        Thread thread1 = new Thread(() -> reservationService.create(member.getId(), request));
        Thread thread2 = new Thread(() -> reservationService.create(anotherMember.getId(), request));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        assertThatThrownBy(() -> reservationService.create(anotherMember.getId(),
                new ReservationRequest(event.getId(), seatRepository.findAll())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 공연에_대한_날짜와_시간을_선택할_수_있다() {
        Event event = eventRepository.save(EventFixture.캣츠());
        LocalDate showDate = event.getSchedules().get(0).getShowDate();
        LocalTime showTime = event.getSchedules().get(0).getShowTime();

        EventDateTimeResponse response = reservationService.readSelectedEventDateTime(event.getId(), showDate, showTime);

        assertNotNull(response);
        assertEquals(event.getId(), response.getId());
        assertEquals(showDate, response.getShowDate());
        assertEquals(showTime, response.getShowTime());
    }

    @Test
    void 공연에_대한_선택한_날짜가_없을_시_예외() {
        Event event = eventRepository.save(EventFixture.캣츠());
        LocalDate wrongDate = event.getSchedules().get(0).getShowDate().plusDays(1);
        LocalTime correctTime = event.getSchedules().get(0).getShowTime();

        assertThatThrownBy(() -> reservationService.readSelectedEventDateTime(event.getId(), wrongDate, correctTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 공연에_대한_선택한_시간이_없을_시_예외() {
        Event event = eventRepository.save(EventFixture.캣츠());
        LocalDate correctDate = event.getSchedules().get(0).getShowDate();
        LocalTime wrongTime = event.getSchedules().get(0).getShowTime().plusHours(1);

        assertThatThrownBy(() -> reservationService.readSelectedEventDateTime(event.getId(), correctDate, wrongTime))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
