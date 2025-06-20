package com.noljo.nolzo.reservation.dto;

import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;

public record ReservationResponse(Long id, ReservationStatus status, int totalPrice, String reservationNumber) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getId(), reservation.getStatus(), reservation.getTotalPrice(),
                reservation.getReservationNumber());
    }
}
