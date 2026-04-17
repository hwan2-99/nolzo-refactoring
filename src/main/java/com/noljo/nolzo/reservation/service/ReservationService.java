package com.noljo.nolzo.reservation.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.global.aop.idempotent.Idempotent;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.repository.PaymentRepository;
import com.noljo.nolzo.queue.application.QueueService;
import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.dto.ReservationCancelResponse;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.reservation.dto.ReservationRequest;
import com.noljo.nolzo.reservation.dto.ReservationResponse;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.service.SeatService;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.service.TicketService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {
    private static final String RESERVATION_NUMBER_PREFIX = "NOLZO";
    private static final int YEAR_SUFFIX_LENGTH = 2;
    private static final int RESERVATION_NUMBER_COUNT = 1;

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final SeatService seatService;
    private final PaymentRepository paymentRepository;
    private final TicketService ticketService;
    private final QueueService queueService;

//    @Transactional
//    @Idempotent(prefix = "reservation:succeed:", key = "#memberId")
//    public ReservationResponse create(Long memberId, ReservationRequest request) {
//        Member member = memberRepository.getOrThrow(memberId);
//        int totalPrice = request.calculateTotalPrice();
//        Reservation reservation = new Reservation(ReservationStatus.PENDING, totalPrice,
//                createReservationNumber(), member);
//
//        seatService.updateWithReservation(request.seats());
//        createTicket(request.seats(), reservation);
//
//        return ReservationResponse.from(reservationRepository.save(reservation));
//    }

    @Transactional
    @Idempotent(prefix = "reservation:", key = "#idemKey")
    public ReservationResponse create(Long memberId, ReservationRequest request, String idemKey) {
        queueService.validateQueue(request.eventId(), memberId);

        Member member = memberRepository.getOrThrow(memberId);
        int totalPrice = request.calculateTotalPrice();

        Reservation reservation = new Reservation(ReservationStatus.PENDING, totalPrice, createReservationNumber(),
                member, idemKey);

        try {
            reservationRepository.saveAndFlush(reservation);

            seatService.updateWithReservation(request.seats());
            createTicket(request.seats(), reservation);

            queueService.markReserved(request.eventId(), memberId);

            return ReservationResponse.from(reservation);

        } catch (DataIntegrityViolationException e) {
            Reservation existing = reservationRepository.findByIdempotencyKey(idemKey)
                    .orElseThrow(() -> e);

            return ReservationResponse.from(existing);

        } catch (Exception e) {
            queueService.leaveEntrance(request.eventId(), memberId);
            throw e;
        }
    }

    private String createReservationNumber() {
        String yearSuffix = String.valueOf(LocalDate.now().getYear()).substring(YEAR_SUFFIX_LENGTH);
        int reservationNumber = (int) reservationRepository.count() + RESERVATION_NUMBER_COUNT;
        String reservationId = String.format("%05d", reservationNumber);

        return RESERVATION_NUMBER_PREFIX + yearSuffix + reservationId;
    }

    @Transactional(readOnly = true)
    public EventDateTimeResponse readSelectedEventDateTime(Long eventId, LocalDate selectDate, LocalTime selectTime) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 존재하지 않습니다."));

        Schedule schedule = event.getSchedules().stream()
                .filter(s -> s.getShowDate().equals(selectDate) && s.getShowTime().equals(selectTime))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("선택한 날짜와 시간의 스케줄이 존재하지 않습니다."));

        return EventDateTimeResponse.fromSchedule(schedule);
    }


    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findReservations(Long memberId) {
        return reservationRepository.findReservationsByMemberId(memberId).stream()
                .map(r -> ReservationEventInfo.of(
                        r.getTickets().get(0).getSeat().getSchedule().getEvent(),
                        r))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findReservationsConfirmed(Long memberId) {
        return reservationRepository.findReservationsStatusConfirmedByMemberId(memberId).stream()
                .map(r -> ReservationEventInfo.of(
                        r.getTickets().get(0).getSeat().getSchedule().getEvent(),
                        r))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findTicketsUsed(Long memberId) {
        return reservationRepository.findTicketStatusUsedByMemberId(memberId).stream()
                .map(r -> ReservationEventInfo.of(
                        r.getTickets().get(0).getSeat().getSchedule().getEvent(),
                        r))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findCancelReservations(Long memberId) {
        List<Reservation> reservations = reservationRepository.findCanceledReservationsFetchAll(memberId);

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
        Reservation reservation = reservationRepository.findReservationDetailsByMemberId(memberId, reservationId);
        Payment payment = paymentRepository.findPaymentByMemberIdAndReservationId(memberId, reservation.getId());

        Event event = reservation.getTickets().stream()
                .findFirst()
                .map(ticket -> ticket.getSeat().getSchedule().getEvent())
                .orElseThrow(() -> new IllegalStateException("예약에 연결된 공연이 없습니다."));

        return ReservationEventInfo.detailsOf(event, reservation, payment);
    }

    private void createTicket(List<Seat> seats, Reservation reservation) {
        for (Seat seat : seats) {
            ticketService.create(reservation, seat.getId());
        }
    }

    @Transactional
    public ReservationCancelResponse cancelReservationById(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보가 없습니다"));

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 예약자만 예약을 취소할 수 있습니다");
        }

        reservation.softDelete();
        reservation.updateStatus(ReservationStatus.CANCELLED);
        reservation.cancelAllTickets();

        return ReservationCancelResponse.from(reservation);
    }
}
