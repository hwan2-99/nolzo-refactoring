package com.noljo.nolzo.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.outbox.domain.OutboxEvent;
import com.noljo.nolzo.outbox.domain.OutboxEventStatus;
import com.noljo.nolzo.outbox.domain.OutboxEventType;
import com.noljo.nolzo.outbox.repository.OutboxEventRepository;
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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Test
    void 예약_취소시_빈자리_이벤트를_outbox에_저장한다() {
        ReservationSetup setup = createPendingReservationWithTicket();

        reservationService.cancelReservationById(setup.memberId(), setup.reservationId());

        List<OutboxEvent> outboxEvents = outboxEventRepository.findAll();
        assertThat(outboxEvents).hasSize(1);
        assertThat(outboxEvents.get(0).getEventType()).isEqualTo(OutboxEventType.SEAT_AVAILABLE);
        assertThat(outboxEvents.get(0).getStatus()).isEqualTo(OutboxEventStatus.PENDING);
    }

    @Test
    void 미결제_예약_자동취소시_빈자리_이벤트를_outbox에_저장한다() {
        createPendingReservationWithTicket();

        reservationService.cancelUnpaidReservations(LocalDateTime.now().plusMinutes(1));

        assertThat(outboxEventRepository.findAll()).isNotEmpty();
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
