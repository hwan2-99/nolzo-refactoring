package com.noljo.nolzo.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.notification.application.port.out.PublishSeatAvailableEventPort;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import com.noljo.nolzo.reservation.service.ReservationService;
import com.noljo.nolzo.schedule.application.port.out.SchedulePersistencePort;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.seat.application.port.out.SeatPersistencePort;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.seat.entity.SectionPrice;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.support.fixture.ScheduleFixture;
import com.noljo.nolzo.ticket.application.port.out.TicketPersistencePort;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.entity.TicketStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@ServiceTest
class ReservationSeatAvailableEventTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MemberPersistencePort memberPersistencePort;

    @Autowired
    private EventPersistencePort eventPersistencePort;

    @Autowired
    private SchedulePersistencePort schedulePersistencePort;

    @Autowired
    private SeatPersistencePort seatPersistencePort;

    @Autowired
    private com.noljo.nolzo.reservation.application.port.out.ReservationPersistencePort reservationPersistencePort;

    @Autowired
    private TicketPersistencePort ticketPersistencePort;

    @MockBean
    private PublishSeatAvailableEventPort publishSeatAvailableEventPort;

    @Test
    void 예약_취소시_빈자리_이벤트를_발행한다() {
        ReservationSetup setup = createPendingReservationWithTicket();

        reservationService.cancelReservationById(setup.memberId(), setup.reservationId());

        ArgumentCaptor<SeatAvailableEvent> captor = ArgumentCaptor.forClass(SeatAvailableEvent.class);
        verify(publishSeatAvailableEventPort, atLeastOnce()).publish(captor.capture());

        SeatAvailableEvent event = captor.getValue();
        assertThat(event.eventId()).isEqualTo(setup.eventId());
        assertThat(event.availableAt()).isNotNull();
    }

    @Test
    void 미결제_예약_자동취소시_빈자리_이벤트를_발행한다() {
        createPendingReservationWithTicket();

        reservationService.cancelUnpaidReservations(LocalDateTime.now().plusMinutes(1));

        ArgumentCaptor<SeatAvailableEvent> captor = ArgumentCaptor.forClass(SeatAvailableEvent.class);
        verify(publishSeatAvailableEventPort, atLeastOnce()).publish(captor.capture());

        assertThat(captor.getAllValues())
                .extracting(SeatAvailableEvent::seatId)
                .isNotEmpty();
    }

    private ReservationSetup createPendingReservationWithTicket() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));
        Seat seat = seatPersistencePort.save(new Seat(
                "A",
                1,
                "1구역",
                "1층",
                SectionPrice.getPriceBySection(1),
                SeatStatus.WAITING,
                schedule
        ));

        Reservation reservation = reservationPersistencePort.save(
                new Reservation(ReservationStatus.PENDING, seat.getPrice(), "NOLZO2600001", member)
        );
        ticketPersistencePort.save(new Ticket(TicketStatus.NOT_USED, reservation, seat));

        return new ReservationSetup(
                member.getId(),
                event.getId(),
                schedule.getId(),
                seat.getId(),
                reservation.getId()
        );
    }

    private record ReservationSetup(
            Long memberId,
            Long eventId,
            Long scheduleId,
            Long seatId,
            Long reservationId
    ) {
    }
}
