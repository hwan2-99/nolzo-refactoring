package com.noljo.nolzo.reservation.dto;

import com.noljo.nolzo.seat.entity.Seat;
import java.util.List;

public record ReservationRequest(Long eventId, List<Seat> seats) {
    public int calculateTotalPrice() {
        return seats.stream()
                .mapToInt(Seat::getPrice)
                .sum();
    }
}
