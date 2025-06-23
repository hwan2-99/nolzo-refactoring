package com.noljo.nolzo.reservation.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.dto.ReservationRequest;
import com.noljo.nolzo.reservation.dto.ReservationResponse;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.seat.service.SeatService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

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

    public EventDateTimeResponse readSelectedEventDateTime(Long eventId , LocalDate selectDate, LocalTime selectTime) {

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 존재하지 않습니다"));

            validateReadSelectedEventDateTime(event,selectDate,selectTime);

            return EventDateTimeResponse.fromEvent(event);
    }

    private void validateReadSelectedEventDateTime(Event event ,LocalDate selectDate, LocalTime selectTime) {

        if (!selectDate.equals(event.getSchedule().getShowDate())) {
            throw new IllegalArgumentException("선택한 이벤트에 유효한 날짜가 존재하지 않습니다.");
        }

        if (!selectTime.equals(event.getSchedule().getShowTime())) {
            throw new IllegalArgumentException("선택한 이벤트에 유효한 시간이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationEventInfo> findReservations(Long memberId) {

        List<Reservation> reservationList = reservationRepository.findReservationsByMemberId(memberId);

        return reservationList.stream()
                .map(reservation -> {
                            Event event = reservation.getTickets().get(0).getSeat().getEvent();
                            return ReservationEventInfo.of(event, reservation);
                        }
                )
                .toList();
    }

    public List<ReservationEventInfo> findReservationsConfirmed(Long memberId) {

        List<Reservation> reservations = reservationRepository.findReservationsStatusConfirmedByMemberId(memberId);


        return reservations.stream()
                .map(reservation -> {
                            Event event = reservation.getTickets().get(0).getSeat().getEvent();
                            return ReservationEventInfo.of(event, reservation);
                        }
                )
                .toList();
    }
  
    public List<ReservationEventInfo> findTicketsUsed(Long memberId) {

        List<Reservation> reservations = reservationRepository.findTicketStatusUsedByMemberId(memberId);
}
