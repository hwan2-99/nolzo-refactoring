package com.noljo.nolzo.reservation.controller;


import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.reservation.dto.*;
import com.noljo.nolzo.reservation.service.ReservationService;
import com.noljo.nolzo.seat.dto.SeatResponse;
import com.noljo.nolzo.seat.service.SeatService;
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

    private final ReservationService reservationService;
    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@AuthenticationPrincipal CustomUserDetails user,
                                                      @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(reservationService.create(user.getMemberId(), request));
    }

    @PostMapping("/{eventId}")

    public ResponseEntity<EventDateTimeResponse> chooseEventDateTime(@PathVariable Long eventId,
                                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectDate,
                                                                     @RequestParam @DateTimeFormat(pattern = "HH:mm:ss") LocalTime selectTime) {
        EventDateTimeResponse event = reservationService.readSelectedEventDateTime(eventId, selectDate, selectTime);
        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<List<ReservationEventInfo>> getReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationEventInfo = reservationService.findReservations(memberId);
        return ResponseEntity.ok(reservationEventInfo);
    }

    @GetMapping("/confirmed")
    public ResponseEntity<List<ReservationEventInfo>> getReservationConfirmed(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationEventInfo = reservationService.findReservationsConfirmed(memberId);
        return ResponseEntity.ok(reservationEventInfo);
    }

    @GetMapping("/used")
    public ResponseEntity<List<ReservationEventInfo>> getTicketsUsed(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationEventInfo = reservationService.findTicketsUsed(memberId);
        return ResponseEntity.ok(reservationEventInfo);
    }

    @GetMapping("/cancel")
    public ResponseEntity<List<ReservationEventInfo>> getCancelReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationEventInfo = reservationService.findCancelReservations(memberId);
        return ResponseEntity.ok(reservationEventInfo);
    }

    @GetMapping("/details/{reservationId}")
    public ResponseEntity<ReservationEventInfo> getReservationDetails(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long reservationId) {
        ReservationEventInfo reservationDetails = reservationService.findReservationDetails(memberId, reservationId);
        return ResponseEntity.ok(reservationDetails);

    }

    @GetMapping("/reservation/{eventId}")
    public ResponseEntity<List<SeatResponse>> findSeatsByEventId(
            @PathVariable Long eventId,
            @RequestParam String date,
            @RequestParam String time) {
        return ResponseEntity.ok(seatService.findSeats(eventId, date, time));
    }

    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<ReservationCancelResponse> cancelReservation(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long reservationId){
        return ResponseEntity.ok(reservationService.cancelReservationById(memberId,reservationId));
    }
}
