package com.noljo.nolzo.reservation.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.global.aop.idempotent.Idempotent;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.notification.application.port.out.PublishSeatAvailableEventPort;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.application.port.out.PaymentPersistencePort;
import com.noljo.nolzo.reservation.application.port.in.ReservationUseCase;
import com.noljo.nolzo.reservation.application.port.out.ReservationQueuePort;
import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.dto.ReservationCancelResponse;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.reservation.dto.ReservationRequest;
import com.noljo.nolzo.reservation.dto.ReservationResponse;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import com.noljo.nolzo.reservation.application.port.out.ReservationPersistencePort;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.seat.application.port.in.SeatUseCase;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.application.port.in.TicketUseCase;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService implements ReservationUseCase {
    private static final String RESERVATION_NUMBER_PREFIX = "NOLZO";
    private static final int YEAR_SUFFIX_LENGTH = 2;
    private static final int RESERVATION_NUMBER_COUNT = 1;

    private final ReservationPersistencePort reservationPersistencePort;
    private final MemberPersistencePort memberPersistencePort;
    private final EventPersistencePort eventPersistencePort;
    private final SeatUseCase seatUseCase;
    private final PaymentPersistencePort paymentPersistencePort;
    private final TicketUseCase ticketUseCase;
    private final ReservationQueuePort reservationQueuePort;
    private final PublishSeatAvailableEventPort publishSeatAvailableEventPort;

//    @Transactional
//    @Idempotent(prefix = "reservation:succeed:", key = "#memberId")
//    public ReservationResponse create(Long memberId, ReservationRequest request) {
//        Member member = memberPersistencePort.getOrThrow(memberId);
//        int totalPrice = request.calculateTotalPrice();
//        Reservation reservation = new Reservation(ReservationStatus.PENDING, totalPrice,
//                createReservationNumber(), member);
//
//        seatUseCase.updateWithReservation(request.seats());
//        createTicket(request.seats(), reservation);
//
//        return ReservationResponse.from(reservationPersistencePort.save(reservation));
//    }

    @Transactional
    @Idempotent(prefix = "reservation:", key = "#idemKey")
    public ReservationResponse create(Long memberId, ReservationRequest request, String idemKey) {
        reservationQueuePort.validateQueue(request.eventId(), memberId);

        Member member = memberPersistencePort.getOrThrow(memberId);
        int totalPrice = request.calculateTotalPrice();

        Reservation reservation = new Reservation(ReservationStatus.PENDING, totalPrice, createReservationNumber(),
                member, idemKey);

        try {
            reservationPersistencePort.saveAndFlush(reservation);

            seatUseCase.updateWithReservation(request.seats());
            createTicket(request.seats(), reservation);

            reservationQueuePort.markReserved(request.eventId(), memberId);

            return ReservationResponse.from(reservation);

        } catch (DataIntegrityViolationException e) {
            Reservation existing = reservationPersistencePort.findByIdempotencyKey(idemKey)
                    .orElseThrow(() -> e);

            return ReservationResponse.from(existing);

        } catch (Exception e) {
            reservationQueuePort.leaveEntrance(request.eventId(), memberId);
            throw e;
        }
    }

    private String createReservationNumber() {
        String yearSuffix = String.valueOf(LocalDate.now().getYear()).substring(YEAR_SUFFIX_LENGTH);
        int reservationNumber = (int) reservationPersistencePort.count() + RESERVATION_NUMBER_COUNT;
        String reservationId = String.format("%05d", reservationNumber);

        return RESERVATION_NUMBER_PREFIX + yearSuffix + reservationId;
    }

    @Transactional(readOnly = true)
    public EventDateTimeResponse readSelectedEventDateTime(Long eventId, LocalDate selectDate, LocalTime selectTime) {

        Event event = eventPersistencePort.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 존재하지 않습니다."));

        Schedule schedule = event.getSchedules().stream()
                .filter(s -> s.getShowDate().equals(selectDate) && s.getShowTime().equals(selectTime))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("선택한 날짜와 시간의 스케줄이 존재하지 않습니다."));

        return EventDateTimeResponse.fromSchedule(schedule);
    }


    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findReservations(Long memberId) {
        return reservationPersistencePort.findReservationsByMemberId(memberId).stream()
                .map(r -> ReservationEventInfo.of(
                        r.getTickets().get(0).getSeat().getSchedule().getEvent(),
                        r))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findReservationsConfirmed(Long memberId) {
        return reservationPersistencePort.findReservationsStatusConfirmedByMemberId(memberId).stream()
                .map(r -> ReservationEventInfo.of(
                        r.getTickets().get(0).getSeat().getSchedule().getEvent(),
                        r))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findTicketsUsed(Long memberId) {
        return reservationPersistencePort.findTicketStatusUsedByMemberId(memberId).stream()
                .map(r -> ReservationEventInfo.of(
                        r.getTickets().get(0).getSeat().getSchedule().getEvent(),
                        r))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findCancelReservations(Long memberId) {
        List<Reservation> reservations = reservationPersistencePort.findCanceledReservationsFetchAll(memberId);

        return reservations.stream()
                .map(reservation -> {
                    Ticket ticket = reservation.getTickets().get(0); // 첫 번째 티켓 기준
                    Event event = ticket.getSeat().getSchedule().getEvent();
                    // 정확한 Schedule 정보는 없음
                    return ReservationEventInfo.of(event, reservation);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationEventInfo findReservationDetails(Long memberId, Long reservationId) {
        Reservation reservation = reservationPersistencePort.findReservationDetailsByMemberId(memberId, reservationId);
        Payment payment = paymentPersistencePort.findPaymentByMemberIdAndReservationId(memberId, reservation.getId());

        Event event = reservation.getTickets().stream()
                .findFirst()
                .map(ticket -> ticket.getSeat().getSchedule().getEvent())
                .orElseThrow(() -> new IllegalStateException("예약에 연결된 공연이 없습니다."));

        return ReservationEventInfo.detailsOf(event, reservation, payment);
    }

    private void createTicket(List<Seat> seats, Reservation reservation) {
        for (Seat seat : seats) {
            ticketUseCase.create(reservation, seat.getId());
        }
    }

    @Transactional
    public ReservationCancelResponse cancelReservationById(Long memberId, Long reservationId) {
        Reservation reservation = reservationPersistencePort.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보가 없습니다"));

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 예약자만 예약을 취소할 수 있습니다");
        }

        reservation.softDelete();
        reservation.updateStatus(ReservationStatus.CANCELLED);
        reservation.cancelAllTickets();
        publishSeatAvailableEvents(reservation.getTickets());

        return ReservationCancelResponse.from(reservation);
    }

    @Transactional
    public void cancelUnpaidReservations(LocalDateTime deadline) {
        List<Reservation> overdueReservations =
                reservationPersistencePort.findByStatusAndCreatedAtBefore(ReservationStatus.PENDING, deadline);

        for (Reservation reservation : overdueReservations) {
            log.info("자동 취소 처리: reservationId = {}", reservation.getId());
            seatUseCase.updateWithPayment(reservation.getTickets(), SeatStatus.AVAILABLE);
            publishSeatAvailableEvents(reservation.getTickets());
            reservationPersistencePort.delete(reservation);
        }
    }

    private void publishSeatAvailableEvents(List<Ticket> tickets) {
        List<SeatAvailableEvent> events = new ArrayList<>();

        for (Ticket ticket : tickets) {
            Seat seat = ticket.getSeat();
            events.add(new SeatAvailableEvent(
                    seat.getSchedule().getEvent().getId(),
                    seat.getSchedule().getId(),
                    seat.getId(),
                    seat.getSeatSection(),
                    LocalDateTime.now()
            ));
        }

        events.forEach(publishSeatAvailableEventPort::publish);
    }
}
