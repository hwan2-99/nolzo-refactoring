package com.noljo.nolzo.seat.dto;

import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;

public record SeatResponse(Long id, String rowName, int seatNumber, String seatSection, String floor, int price,
                           SeatStatus status) {

    public static SeatResponse from(Seat seat) {
        return new SeatResponse(seat.getId(), seat.getRowName(), seat.getSeatNumber(), seat.getSeatSection(),
                seat.getFloor(), seat.getPrice(), seat.getStatus());
    }
}
