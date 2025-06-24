package com.noljo.nolzo.reservation.service;

import com.noljo.nolzo.Schedule.entity.Schedule;
import com.noljo.nolzo.event.dto.ReservationEvent;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.repository.PaymentRepository;
import com.noljo.nolzo.reservation.dto.*;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.service.SeatService;
import java.time.LocalDate;

import com.noljo.nolzo.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.HashSet;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Transactional
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

    //todo Permistic lock을 사용해서 구현한 내용 추후 multi-thread or Optimistic Lock or Redis 사용후 비교예정
    public ReservationResponse create(Long memberId, ReservationRequest request) {
        Member member = memberRepository.getOrThrow(memberId);
        Reservation reservation = new Reservation(ReservationStatus.PENDING, request.calculateTotalPrice(),
                createReservationNumber(), member);

        seatService.updateWithReservation(request.seats());
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private String createReservationNumber() {
        String yearSuffix = String.valueOf(LocalDate.now().getYear()).substring(YEAR_SUFFIX_LENGTH);
        int reservationNumber = (int) reservationRepository.count() + RESERVATION_NUMBER_COUNT;
        String reservationId = String.format("%05d", reservationNumber);

        return RESERVATION_NUMBER_PREFIX + yearSuffix + reservationId;
    }

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
                        r.getTickets().get(0).getSeat().getEvent(),
                        r))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findReservationsConfirmed(Long memberId) {
        return reservationRepository.findReservationsStatusConfirmedByMemberId(memberId).stream()
                .map(r -> ReservationEventInfo.of(
                        r.getTickets().get(0).getSeat().getEvent(),
                        r))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findTicketsUsed(Long memberId) {
        return reservationRepository.findTicketStatusUsedByMemberId(memberId).stream()
                .map(r -> ReservationEventInfo.of(
                        r.getTickets().get(0).getSeat().getEvent(),
                        r))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findCancelReservations(Long memberId) {
        List<Reservation> reservations = reservationRepository.findCanceledReservationsFetchAll(memberId);

        return reservations.stream()
                .map(reservation -> {
                    Ticket ticket = reservation.getTickets().get(0); // 첫 번째 티켓 기준
                    Event event = ticket.getSeat().getEvent();
                    // 정확한 Schedule 정보는 없음
                    return ReservationEventInfo.of(event, reservation);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationEventInfo findReservationDetails(Long memberId) {
        Reservation reservation = reservationRepository.findReservationDetailsByMemberId(memberId);
        Payment payment = paymentRepository.findPaymentByMemberIdAndReservationId(memberId, reservation.getId());

        Event event = reservation.getTickets().stream()
                .findFirst()
                .map(ticket -> ticket.getSeat().getEvent())
                .orElseThrow(() -> new IllegalStateException("예약에 연결된 공연이 없습니다."));

        return ReservationEventInfo.detailsOf(event, reservation, payment);
    }
}

