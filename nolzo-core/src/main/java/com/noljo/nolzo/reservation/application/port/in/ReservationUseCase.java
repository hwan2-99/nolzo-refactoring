package com.noljo.nolzo.reservation.application.port.in;

import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.dto.ReservationCancelResponse;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.reservation.dto.ReservationRequest;
import com.noljo.nolzo.reservation.dto.ReservationResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ReservationUseCase {

    ReservationResponse create(Long memberId, ReservationRequest request, String idemKey);

    EventDateTimeResponse readSelectedEventDateTime(Long eventId, LocalDate selectDate, LocalTime selectTime);

    List<ReservationEventInfo> findReservations(Long memberId);

    List<ReservationEventInfo> findReservationsConfirmed(Long memberId);

    List<ReservationEventInfo> findTicketsUsed(Long memberId);

    List<ReservationEventInfo> findCancelReservations(Long memberId);

    ReservationEventInfo findReservationDetails(Long memberId, Long reservationId);

    ReservationCancelResponse cancelReservationById(Long memberId, Long reservationId);

    void cancelUnpaidReservations(LocalDateTime deadline);
}
