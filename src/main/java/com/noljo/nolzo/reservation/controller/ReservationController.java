package com.noljo.nolzo.reservation.controller;

import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

}
