package com.noljo.nolzo.reservation.controller;


import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/{eventId}")
    public ResponseEntity<EventDateTimeResponse> chooseEventDateTime(@PathVariable Long eventId ,
                                                                     @RequestParam LocalDate selectDate,
                                                                     @RequestParam LocalTime selectTime) {

        EventDateTimeResponse event = reservationService.readSelectedEventDateTime(eventId, selectDate, selectTime);
        return ResponseEntity.ok(event);
    }
  
    @GetMapping
    public ResponseEntity<List<ReservationEventInfo>> getReservations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationDetails = reservationService.findReservations(memberId);
        return ResponseEntity.ok(reservationDetails);
    }

    @GetMapping("/confirmed")
    public ResponseEntity<List<ReservationEventInfo>> getReservationConfirmed(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationDetails = reservationService.findReservationsConfirmed(memberId);
        return  ResponseEntity.ok(reservationDetails);

    }

    @GetMapping("/used")
    public ResponseEntity<List<ReservationEventInfo>> getTicketsUsed(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationDetails = reservationService.findTicketsUsed(memberId);
        return ResponseEntity.ok(reservationDetails);
    }
  
    @GetMapping("/cancel")
    public ResponseEntity<List<ReservationEventInfo>> getCancelReservations(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationDetails = reservationService.findCancelReservations(memberId);
        return  ResponseEntity.ok(reservationDetails);
    }
}
