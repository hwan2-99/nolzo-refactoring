package com.noljo.nolzo.seat.dto;

import com.noljo.nolzo.seat.entity.SeatStatus;

public record SeatResponse(Long id, String rowName, int seatNumber, String seatSection, String floor, int price,
                           SeatStatus status) {
}
