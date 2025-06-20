package com.noljo.nolzo.reservation.controller;


import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{eventId}")
    public ResponseEntity<EventDateTimeResponse> chooseEventDateTime(@PathVariable Long eventId) {

        EventDateTimeResponse event = reservationService.chooseEventDateTime(eventId);
        return ResponseEntity.ok(event);
    }
  
    @GetMapping
    public ResponseEntity<List<ReservationEventInfo>> getReservations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationEventInfo> reservationDetails = reservationService.findReservations(memberId);
        return ResponseEntity.ok(reservationDetails);
    }

}
