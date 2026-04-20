package com.noljo.nolzo.domain.ticket.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.schedule.repository.ScheduleRepository;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.repository.SeatRepository;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.support.fixture.ReservationFixture;
import com.noljo.nolzo.support.fixture.ScheduleFixture;
import com.noljo.nolzo.support.fixture.SeatFixture;
import com.noljo.nolzo.support.fixture.TicketFixture;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.entity.TicketStatus;
import com.noljo.nolzo.ticket.repository.TicketRepository;
import com.noljo.nolzo.ticket.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ServiceTest
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
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    void 티켓을_생성할_수_있다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        Event event = EventFixture.캣츠();
        eventRepository.save(event);

        Schedule schedule = ScheduleFixture.공연_스케쥴(event);
        scheduleRepository.save(schedule);

        Seat seat = SeatFixture.일반좌석(schedule);
        seatRepository.save(seat);

        Reservation reservation = ReservationFixture.예약(member);
        reservationRepository.save(reservation);

        Ticket ticket = TicketFixture.미사용티켓(reservation, seat);
        ticketRepository.save(ticket);

        assertThat(ticketRepository.findAll()).hasSize(1);
    }

    @Test
    void 공연시간_30분이_지나면_티켓상태_관람완료_변경() {
        // given
        Event event = eventRepository.save(EventFixture.햄릿());
        Schedule schedule = scheduleRepository.save(ScheduleFixture.공연_스케쥴(event));
        Seat seat = seatRepository.save(SeatFixture.일반좌석(schedule));

        Member member = memberRepository.save(MemberFixture.회원());
        Reservation reservation = reservationRepository.save(ReservationFixture.예약(member));

        Ticket ticket = TicketFixture.미사용티켓(reservation, seat);
        ticketRepository.save(ticket);

        // 공연 시작 시간 + 31분을 기준 시간으로 설정
        LocalDate baseDate = schedule.getShowDate();
        LocalTime baseTime = schedule.getShowTime().plusMinutes(31);

        // when
        ticketService.updateTicketStatusUsed(baseDate,baseTime);

        // then
        Ticket updated = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertEquals(TicketStatus.USED, updated.getStatus());
    }
}
