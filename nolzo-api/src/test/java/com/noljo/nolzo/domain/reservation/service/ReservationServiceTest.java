package com.noljo.nolzo.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.global.error.exception.SeatException;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.reservation.dto.ReservationRequest;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import com.noljo.nolzo.reservation.application.port.out.ReservationPersistencePort;
import com.noljo.nolzo.reservation.service.ReservationService;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.schedule.application.port.out.SchedulePersistencePort;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.application.port.out.SeatPersistencePort;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.*;
import com.noljo.nolzo.ticket.entity.TicketStatus;
import com.noljo.nolzo.ticket.application.port.out.TicketPersistencePort;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
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
    private EventPersistencePort eventPersistencePort;
    @Autowired
    private MemberPersistencePort memberPersistencePort;
    @Autowired
    private SeatPersistencePort seatPersistencePort;
    @Autowired
    private SchedulePersistencePort schedulePersistencePort;
    @Autowired
    private ReservationPersistencePort reservationPersistencePort;
    @Autowired
    private TicketPersistencePort ticketPersistencePort;

    @Test
    void 같은_좌석은_동시에_접근이_불가능하다() throws InterruptedException {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Member anotherMember = memberPersistencePort.save(MemberFixture.회투());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        Seat seat1 = seatPersistencePort.save(SeatFixture.일반좌석(schedule));

        List<Seat> seats = List.of(seat1);
        ReservationRequest request = new ReservationRequest(event.getId(), seats);

        AtomicReference<Throwable> thread1Error = new AtomicReference<>();
        AtomicReference<Throwable> thread2Error = new AtomicReference<>();

        Thread thread1 = new Thread(() -> {
            try {
                reservationService.create(member.getId(), request, "test");
            } catch (Throwable t) {
                thread1Error.set(t);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                reservationService.create(anotherMember.getId(), request, "test2");
            } catch (Throwable t) {
                thread2Error.set(t);
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        Throwable e1 = thread1Error.get();
        Throwable e2 = thread2Error.get();

        List<Throwable> errors = Arrays.asList(e1, e2);

        assertThat(errors).anyMatch(Objects::isNull);
        assertThat(errors).anyMatch(e -> e instanceof SeatException);
    }

    @Test
    void 공연에_대한_날짜와_시간을_선택할_수_있다() {
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));
        event.addSchedule(schedule);

        EventDateTimeResponse response = reservationService.readSelectedEventDateTime(event.getId(),
                schedule.getShowDate(),
                schedule.getShowTime());

        assertNotNull(response);
        assertEquals(event.getId(), response.getId());
        assertEquals(schedule.getShowDate(), response.getShowDate());
        assertEquals(schedule.getShowTime(), response.getShowTime());
    }

    @Test
    void 공연에_대한_선택한_날짜가_없을_시_예외() {
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));
        event.addSchedule(schedule);

        LocalDate wrongDate = schedule.getShowDate().plusDays(1);
        LocalTime correctTime = schedule.getShowTime();

        assertThatThrownBy(() -> reservationService.readSelectedEventDateTime(event.getId(), wrongDate, correctTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 공연에_대한_선택한_시간이_없을_시_예외() {
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));
        event.addSchedule(schedule);

        LocalDate correctDate = schedule.getShowDate();
        LocalTime wrongTime = schedule.getShowTime().plusHours(1);

        assertThatThrownBy(() -> reservationService.readSelectedEventDateTime(event.getId(), correctDate, wrongTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 예매한_공연에_대해서_취소() {
        //given
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Reservation reservation = reservationPersistencePort.save(ReservationFixture.예약(member));
        Reservation reservation2 = reservationPersistencePort.save(ReservationFixture.예약2(member));

        //when
        reservationService.cancelReservationById(member.getId(), reservation.getId());
        //then
        Reservation cancelledReservation = reservationPersistencePort.findById(reservation.getId())
                .orElseThrow(() -> new AssertionError("취소된 예약을 찾을 수 없습니다."));

        assertEquals(ReservationStatus.CANCELLED, cancelledReservation.getStatus());
        reservation.getTickets().forEach(ticket ->
                assertEquals(TicketStatus.CANCELLED, ticket.getStatus()));
        reservation2.getTickets().forEach(ticket ->
                assertEquals(TicketStatus.NOT_USED, ticket.getStatus()));
    }
}
