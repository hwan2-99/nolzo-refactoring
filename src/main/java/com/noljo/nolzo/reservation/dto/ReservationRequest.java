package com.noljo.nolzo.reservation.dto;

import java.util.List;

public record ReservationRequest(
        Long eventId,
        List<Long> seatIds) {
}
