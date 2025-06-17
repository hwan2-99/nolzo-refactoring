package com.noljo.nolzo.reservation.dto;

import com.noljo.nolzo.seat.entity.Seat;
import java.util.List;

public record ReservationRequest(Long memberId, Long eventId, List<Seat> seats) {
}
