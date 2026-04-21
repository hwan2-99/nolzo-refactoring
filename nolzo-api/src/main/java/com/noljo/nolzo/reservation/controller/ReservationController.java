package com.noljo.nolzo.reservation.controller;


import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.reservation.dto.*;
import com.noljo.nolzo.reservation.application.port.in.ReservationUseCase;
import com.noljo.nolzo.seat.dto.SeatResponse;
import com.noljo.nolzo.seat.application.port.in.SeatUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationUseCase reservationUseCase;
    private final SeatUseCase seatUseCase;

    @PostMapping()
    public ResponseEntity<ReservationResponse> create(
            @RequestHeader("Idempotency-Key") String idemKey,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ReservationRequest request
    ) {
        return ResponseEntity.ok(reservationUseCase.create(user.getMemberId(), request, idemKey));
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<EventDateTimeResponse> chooseEventDateTime(@PathVariable Long eventId,
                                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectDate,
                                                                     @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime selectTime) {
        EventDateTimeResponse event = reservationUseCase.readSelectedEventDateTime(eventId, selectDate, selectTime);
        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<List<ReservationEventInfo>> getReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationEventInfo = reservationUseCase.findReservations(memberId);
        return ResponseEntity.ok(reservationEventInfo);
    }

    @GetMapping("/confirmed")
    public ResponseEntity<List<ReservationEventInfo>> getReservationConfirmed(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationEventInfo = reservationUseCase.findReservationsConfirmed(memberId);
        return ResponseEntity.ok(reservationEventInfo);
    }

    @GetMapping("/used")
    public ResponseEntity<List<ReservationEventInfo>> getTicketsUsed(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationEventInfo = reservationUseCase.findTicketsUsed(memberId);
        return ResponseEntity.ok(reservationEventInfo);
    }

    @GetMapping("/cancel")
    public ResponseEntity<List<ReservationEventInfo>> getCancelReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationEventInfo = reservationUseCase.findCancelReservations(memberId);
        return ResponseEntity.ok(reservationEventInfo);
    }

    @GetMapping("/details/{reservationId}")
    public ResponseEntity<ReservationEventInfo> getReservationDetails(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long reservationId) {
        ReservationEventInfo reservationDetails = reservationUseCase.findReservationDetails(memberId, reservationId);
        return ResponseEntity.ok(reservationDetails);

    }

    @GetMapping("/reservation/{eventId}")
    public ResponseEntity<List<SeatResponse>> findSeatsByEventId(
            @PathVariable(name = "eventId") Long eventId,
            @RequestParam(name = "date") String date,
            @RequestParam(name = "time") String time) {
        return ResponseEntity.ok(seatUseCase.findSeats(eventId, date, time));
    }

    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<ReservationCancelResponse> cancelReservation(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationUseCase.cancelReservationById(memberId, reservationId));
    }
}
