package com.noljo.nolzo.domain.ticket.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.application.port.out.ReservationPersistencePort;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.schedule.application.port.out.SchedulePersistencePort;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.application.port.out.SeatPersistencePort;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.support.fixture.ReservationFixture;
import com.noljo.nolzo.support.fixture.ScheduleFixture;
import com.noljo.nolzo.support.fixture.SeatFixture;
import com.noljo.nolzo.support.fixture.TicketFixture;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.entity.TicketStatus;
import com.noljo.nolzo.ticket.application.port.out.TicketPersistencePort;
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
    private MemberPersistencePort memberPersistencePort;
    @Autowired
    private TicketPersistencePort ticketPersistencePort;
    @Autowired
    private ReservationPersistencePort reservationPersistencePort;
    @Autowired
    private SeatPersistencePort seatPersistencePort;
    @Autowired
    private EventPersistencePort eventPersistencePort;
    @Autowired
    private SchedulePersistencePort schedulePersistencePort;

    @Test
    void 티켓을_생성할_수_있다() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        Event event = EventFixture.캣츠();
        eventPersistencePort.save(event);

        Schedule schedule = ScheduleFixture.공연_스케쥴(event);
        schedulePersistencePort.save(schedule);

        Seat seat = SeatFixture.일반좌석(schedule);
        seatPersistencePort.save(seat);

        Reservation reservation = ReservationFixture.예약(member);
        reservationPersistencePort.save(reservation);

        Ticket ticket = TicketFixture.미사용티켓(reservation, seat);
        ticketPersistencePort.save(ticket);

        assertThat(ticketPersistencePort.findAll()).hasSize(1);
    }

    @Test
    void 공연시간_30분이_지나면_티켓상태_관람완료_변경() {
        // given
        Event event = eventPersistencePort.save(EventFixture.햄릿());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));
        Seat seat = seatPersistencePort.save(SeatFixture.일반좌석(schedule));

        Member member = memberPersistencePort.save(MemberFixture.회원());
        Reservation reservation = reservationPersistencePort.save(ReservationFixture.예약(member));

        Ticket ticket = TicketFixture.미사용티켓(reservation, seat);
        ticketPersistencePort.save(ticket);

        // 공연 시작 시간 + 31분을 기준 시간으로 설정
        LocalDate baseDate = schedule.getShowDate();
        LocalTime baseTime = schedule.getShowTime().plusMinutes(31);

        // when
        ticketService.updateTicketStatusUsed(baseDate,baseTime);

        // then
        Ticket updated = ticketPersistencePort.findById(ticket.getId()).orElseThrow();
        assertEquals(TicketStatus.USED, updated.getStatus());
    }
}
