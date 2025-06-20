package com.noljo.nolzo.domain.reservation.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.dto.ReservationResponse;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void 전체_예약_조회() throws Exception {
        //given
        Event event1 = eventRepository.save(EventFixture.캣츠());
        Event event2 = eventRepository.save(EventFixture.햄릿());
        Member user = memberRepository.save(MemberFixture.회원());
        Seat seat1 = seatRepository.save(SeatFixture.일반좌석(event1));
        Seat seat2 = seatRepository.save(SeatFixture.일반좌석(event2));
        Reservation reservation1 = reservationRepository.save(ReservationFixture.예약(user));
        Reservation reservation2 = reservationRepository.save(ReservationFixture.예약2(user));
        ticketRepository.save(TicketFixture.미사용티켓(reservation1,seat1));
        ticketRepository.save(TicketFixture.미사용티켓(reservation2,seat2));

        //when
        List<ReservationResponse> reservations = reservationService.findReservations(user.getId());

        //then
        assertThat(reservations).hasSize(2);
        assertThat(reservations.get(0).getEvent().getTitle()).isIn("Cats", "Hamlet");
        assertThat(reservations.get(1).getDetail().getReservationNumber()).isIn("12313123L", "1322313123L");
        }
}
