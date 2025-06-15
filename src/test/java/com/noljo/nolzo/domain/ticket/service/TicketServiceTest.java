package com.noljo.nolzo.domain.ticket.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.repository.SeatRepository;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.support.fixture.ReservationFixture;
import com.noljo.nolzo.support.fixture.SeatFixture;
import com.noljo.nolzo.support.fixture.TicketFixture;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.repository.TicketRepository;
import com.noljo.nolzo.ticket.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TicketServiceTest {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        reservationRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void 티켓을_생성할_수_있다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);
        System.out.println(member.getId());

        Event event = EventFixture.캣츠();
        eventRepository.save(event);
        System.out.println(event.getId());

        Seat seat = SeatFixture.일반좌석(event);
        seatRepository.save(seat);
        System.out.println("Seat ID: " + seat.getId());

        Reservation reservation = ReservationFixture.예약(member);
        reservationRepository.save(reservation);
        System.out.println(reservation.getId());

        Ticket ticket = TicketFixture.미사용티켓(reservation, seat);
        ticketRepository.save(ticket);
        System.out.println(ticket.getId());

        assertThat(ticketRepository.findAll()).hasSize(1);
    }
}
