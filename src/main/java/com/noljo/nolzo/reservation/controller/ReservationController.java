package com.noljo.nolzo.reservation.controller;

import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.reservation.dto.ReservationResponse;
import com.noljo.nolzo.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ReservationResponse> reservationDetails = reservationService.findReservations(memberId);
        return ResponseEntity.ok(reservationDetails);
    }
}
