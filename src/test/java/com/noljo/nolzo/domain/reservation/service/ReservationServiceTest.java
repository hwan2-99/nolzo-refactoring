package com.noljo.nolzo.domain.reservation.service;


import static org.assertj.core.api.Assertions.assertThat;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.event.service.EventService;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.dto.ReservationRequest;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    @Autowired
    private EventService eventService;

    @Test
    void 같은_좌석은_동시에_접근이_불가능하다() throws InterruptedException {
        Member member = MemberFixture.회원();
        memberRepository.save(member);
        Member anotherMember = MemberFixture.회투();
        memberRepository.save(anotherMember);

        Event event = EventFixture.캣츠();
        eventRepository.save(event);

        Seat seat = SeatFixture.일반좌석(event);
        seatRepository.save(seat);
        Seat seat2 = SeatFixture.일반좌석2(event);
        seatRepository.save(seat2);

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
    public void 공연에_대한_날짜와_시간을_선택할_수_있다() {
        // given
        Event event = eventRepository.save(EventFixture.캣츠()); // showDate: 2025-12-01, showTime: 19:00

        // when
        EventDateTimeResponse response = reservationService.readSelectedEventDateTime(
                event.getId(),
                event.getSchedule().getShowDate(),
                event.getSchedule().getShowTime()
        );

        // then
        assertNotNull(response);
        assertEquals(event.getId(), response.getId());
        assertEquals(event.getSchedule().getShowDate(), response.getShowdate());
        assertEquals(event.getSchedule().getShowTime(), response.getShowTime());
    }

    @Test
    public void 공연에_대한_선택한_날짜가_없을_시_예외() {
        // given
        Event event = eventRepository.save(EventFixture.캣츠());

        LocalDate wrongDate = event.getSchedule().getShowDate().plusDays(1);
        LocalTime correctTime = event.getSchedule().getShowTime();

        // when & then
        assertThatThrownBy(() ->
                reservationService.readSelectedEventDateTime(event.getId(), wrongDate, correctTime)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void 공연에_대한_선택한_시간이_없을_시_예외() {
        // given
        Event event = eventRepository.save(EventFixture.캣츠());

        LocalDate correctDate = event.getSchedule().getShowDate();
        LocalTime wrongTime = event.getSchedule().getShowTime().plusHours(1);

        // when & then

        assertThatThrownBy(()->reservationService.readSelectedEventDateTime(event.getId(),correctDate,wrongTime))
                .isInstanceOf(IllegalArgumentException.class);
    }



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
        List<ReservationEventInfo> reservations = reservationService.findReservations(user.getId());

        //then
        assertThat(reservations).hasSize(2);
        assertThat(reservations.get(0).getEvent().getTitle()).isIn("Cats", "Hamlet");
        assertThat(reservations.get(1).getDetail().getReservationNumber()).isIn("12313123L", "1322313123L");
        }
}
