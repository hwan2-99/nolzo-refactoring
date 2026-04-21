package com.noljo.nolzo.reservation.dto;

import com.noljo.nolzo.reservation.entity.Reservation;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationCancelResponse {

    private Long id;
    private String status;

    public static ReservationCancelResponse from(Reservation reservation) {
        return ReservationCancelResponse.builder()
                .id(reservation.getId())
                .status(reservation.getStatus().name())
                .build();
    }
}
